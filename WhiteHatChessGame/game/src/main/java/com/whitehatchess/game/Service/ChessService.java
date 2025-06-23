package com.whitehatchess.game.Service;

import com.whitehatchess.game.Domain.*;
import com.whitehatchess.game.Exceptions.InvalidMoveException;
import org.springframework.stereotype.Service;

@Service
public class ChessService {
    private final Board board;

    public ChessService(Board board) {
        this.board = board;
    }

    // Special move handlers --------------------------------------------------

    public boolean attemptCastling(ChessMove move, Player currentPlayer) throws InvalidMoveException {
        Position from = move.getFrom();
        Position to = move.getTo();
        Piece king = board.getGrid()[from.getX()][from.getY()];

        // Validate basic castling conditions
        if (king == null || king.getType() != PieceType.KING || king.isHasMoved()) {
            return false;
        }

        int direction = to.getX() > from.getX() ? 1 : -1; // Kingside or queenside
        int rookX = direction > 0 ? 7 : 0;
        Piece rook = board.getGrid()[rookX][from.getY()];

        if (rook == null || rook.getType() != PieceType.ROOK || rook.isHasMoved()) {
            return false;
        }

        // Check path is clear and not under attack
        for (int x = from.getX() + direction; x != rookX; x += direction) {
            if (board.getGrid()[x][from.getY()] != null) {
                return false;
            }
            if (isSquareUnderAttack(new Position(x, from.getY()), currentPlayer)) {
                return false;
            }
        }

        // Execute castling
        board.applyMove(new ChessMove(from, to));
        board.applyMove(new ChessMove(
                new Position(rookX, from.getY()),
                new Position(from.getX() + direction, from.getY())
        ));
        return true;
    }

    public boolean attemptEnPassant(ChessMove move, Player currentPlayer) {
        Position from = move.getFrom();
        Position to = move.getTo();
        Piece pawn = board.getGrid()[from.getX()][from.getY()];

        // Validate en passant conditions
        if (pawn == null || pawn.getType() != PieceType.PAWN) {
            return false;
        }

        int direction = currentPlayer == Player.WHITE ? 1 : -1;
        if (Math.abs(from.getX() - to.getX()) == 1 && to.getY() == from.getY() + direction) {
            Piece adjacentPawn = board.getGrid()[to.getX()][from.getY()];
            if (adjacentPawn != null && adjacentPawn.getType() == PieceType.PAWN &&
                    adjacentPawn.getPlayer() != currentPlayer) {
                // Execute en passant
                board.applyMove(move);
                board.getGrid()[to.getX()][from.getY()] = null; // Remove captured pawn
                return true;
            }
        }
        return false;
    }

    public void promotePawn(Position pos, PieceType promotionType) throws InvalidMoveException {
        Piece pawn = board.getGrid()[pos.getX()][pos.getY()];
        if (pawn == null || pawn.getType() != PieceType.PAWN) {
            throw new InvalidMoveException("No pawn at promotion square");
        }

        if ((pos.getY() == 7 && pawn.getPlayer() == Player.WHITE) ||
                (pos.getY() == 0 && pawn.getPlayer() == Player.BLACK)) {
            board.getGrid()[pos.getX()][pos.getY()] = new Piece(promotionType, pawn.getPlayer());
        } else {
            throw new InvalidMoveException("Pawn not in promotion position");
        }
    }

    // Helper methods ---------------------------------------------------------

    private boolean isSquareUnderAttack(Position pos, Player defender) {
        Player attacker = defender.opponent();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getGrid()[x][y];
                if (piece != null && piece.getPlayer() == attacker) {
                    ChessMove hypothetical = new ChessMove(new Position(x, y), pos);
                    if (board.isValidPieceMove(piece, new Position(x, y), pos, true)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Removed redundant methods:
    // - isValidMove()
    // - processMove()
    // - All individual piece validation methods (now handled by Board class)
}