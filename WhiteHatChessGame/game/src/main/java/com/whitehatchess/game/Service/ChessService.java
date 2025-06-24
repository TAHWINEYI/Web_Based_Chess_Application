package com.whitehatchess.game.Service;

import com.whitehatchess.game.Domain.*;
import com.whitehatchess.game.Exceptions.InvalidMoveException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ChessService {
    private final Board board;
    private final Game game;

    public ChessService(Board board, Game game) {
        this.board = board;
        this.game = game;
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

    public void makeMove(String move, Model model) {
        try {
            // Fixed coordinate conversion (0-based indexing)
            int startX = move.charAt(0) - 'a';
            int startY = 8 - Character.getNumericValue(move.charAt(1));
            int endX = move.charAt(2) - 'a';
            int endY = 8 - Character.getNumericValue(move.charAt(3));

            ChessMove chessMove = new ChessMove(
                    new Position(startX, startY),
                    new Position(endX, endY)
            );
            game.makeMove(chessMove);
        } catch (Exception e) {
            game.getBoard().setStatusMessage("Invalid move: " + e.getMessage());
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

    public void loadFileMoves(MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload");
            }

            // Read file content
            String content = new String(file.getBytes());
            String[] moves = content.split("\\s+"); // Split by whitespace

            // Process each move
            for (String move : moves) {
                if (!move.matches("[a-h][1-8][a-h][1-8]")) {
                    model.addAttribute("error", "Invalid move format in file: " + move);
                }

                try {
                    int startX = move.charAt(0) - 'a';
                    int startY = 8 - Character.getNumericValue(move.charAt(1));
                    int endX = move.charAt(2) - 'a';
                    int endY = 8 - Character.getNumericValue(move.charAt(3));

                    ChessMove chessMove = new ChessMove(
                            new Position(startX, startY),
                            new Position(endX, endY)
                    );
                    game.makeMove(chessMove);
                } catch (InvalidMoveException e) {
                    model.addAttribute("error", "Invalid move from file: " + move + " - " + e.getMessage());
                }
            }
            model.addAttribute("success", "Moves loaded successfully from file!");
        } catch (IOException e) {
            model.addAttribute("error", "Failed to read file: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Error processing file: " + e.getMessage());
        }
    }

    public void promotePawns(String pos, String pieceType, Model model) {
        try {
            // Validate input format
            if (pos == null || pos.length() != 2) {
                throw new IllegalArgumentException("Position must be 2 characters (e.g., 'e8')");
            }
            if (pieceType == null || pieceType.isEmpty()) {
                throw new IllegalArgumentException("Piece type must be specified");
            }

            // Convert position (e.g., "e8" to Position)
            int x = pos.charAt(0) - 'a';
            int y = 8 - Character.getNumericValue(pos.charAt(1));
            if (x < 0 || x > 7 || y < 0 || y > 7) {
                throw new IllegalArgumentException("Invalid position on board");
            }
            Position position = new Position(x, y);

            // Convert piece type string to enum
            PieceType type;
            try {
                type = PieceType.valueOf(pieceType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid piece type. Use: QUEEN, ROOK, BISHOP, or KNIGHT");
            }

            // Validate promotion piece (cannot promote to king or pawn)
            if (type == PieceType.KING || type == PieceType.PAWN) {
                throw new IllegalArgumentException("Cannot promote to " + type);
            }

            // Attempt promotion
            promotePawn(position, type);
            model.addAttribute("successMessage", "Pawn promoted to " + type);

        } catch (InvalidMoveException e) {
            model.addAttribute("errorMessage", "Promotion failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error during promotion");
        }
    }
}