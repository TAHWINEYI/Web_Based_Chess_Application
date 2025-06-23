package com.whitehatchess.game.Domain;

import com.whitehatchess.game.Domain.Piece;
import com.whitehatchess.game.Domain.Player;
import org.springframework.stereotype.Component;

@Component
public class Board {
    private Piece[][] grid;
    private String[][] emojiGrid; // Precomputed emoji representation
    private String statusMessage;
    private String moveHistory;
    private Position whiteKingPosition;
    private Position blackKingPosition;
    private boolean isWhiteTurn = false;

    public Board() {
        this.grid = new Piece[8][8];
        this.emojiGrid = new String[8][8];
        this.statusMessage = "";
        this.moveHistory = "";
        initialize();
    }

    public void initialize() {
        // Clear the board
        grid = new Piece[8][8];
        emojiGrid = new String[8][8];

        // Set up white pieces
        grid[0][0] = new Piece(PieceType.ROOK, Player.WHITE);
        grid[1][0] = new Piece(PieceType.KNIGHT, Player.WHITE);
        grid[2][0] = new Piece(PieceType.BISHOP, Player.WHITE);
        grid[3][0] = new Piece(PieceType.QUEEN, Player.WHITE);
        grid[4][0] = new Piece(PieceType.KING, Player.WHITE);
        whiteKingPosition = new Position(4, 0);
        grid[5][0] = new Piece(PieceType.BISHOP, Player.WHITE);
        grid[6][0] = new Piece(PieceType.KNIGHT, Player.WHITE);
        grid[7][0] = new Piece(PieceType.ROOK, Player.WHITE);

        for (int i = 0; i < 8; i++) {
            grid[i][1] = new Piece(PieceType.PAWN, Player.WHITE);
        }

        // Set up black pieces
        grid[0][7] = new Piece(PieceType.ROOK, Player.BLACK);
        grid[1][7] = new Piece(PieceType.KNIGHT, Player.BLACK);
        grid[2][7] = new Piece(PieceType.BISHOP, Player.BLACK);
        grid[3][7] = new Piece(PieceType.QUEEN, Player.BLACK);
        grid[4][7] = new Piece(PieceType.KING, Player.BLACK);
        blackKingPosition = new Position(4, 7);
        grid[5][7] = new Piece(PieceType.BISHOP, Player.BLACK);
        grid[6][7] = new Piece(PieceType.KNIGHT, Player.BLACK);
        grid[7][7] = new Piece(PieceType.ROOK, Player.BLACK);

        for (int i = 0; i < 8; i++) {
            grid[i][6] = new Piece(PieceType.PAWN, Player.BLACK);
        }

        statusMessage = "Game started. White's turn";
        moveHistory = "";

        // Precompute emoji grid
        updateEmojiGrid();
    }

    private void updateEmojiGrid() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = grid[x][y];
                emojiGrid[x][y] = piece != null ? piece.toEmoji() : "⬜"; // Default empty square
            }
        }
    }

    public void applyMove(ChessMove move) {
        Piece piece = grid[move.getFrom().getX()][move.getFrom().getY()];
        grid[move.getFrom().getX()][move.getFrom().getY()] = null;
        grid[move.getTo().getX()][move.getTo().getY()] = piece;

        // Update king position if moved
        if (piece.getType() == PieceType.KING) {
            if (piece.getPlayer() == Player.WHITE) {
                whiteKingPosition = move.getTo();
            } else {
                blackKingPosition = move.getTo();
            }
        }

        // Update emoji grid after move
        updateEmojiGrid();
    }

    public String[][] getEmojiGrid() {
        return emojiGrid;
    }

    public boolean isValidMove(ChessMove move, Player currentPlayer) {
        Position from = move.getFrom();
        Position to = move.getTo();

        if (!isValidPosition(from) || !isValidPosition(to)) {
            return false;
        }

        Piece piece = grid[from.getX()][from.getY()];
        Piece targetPiece = grid[to.getX()][to.getY()];

        if (piece == null || piece.getPlayer() != currentPlayer) {
            return false;
        }

        if (targetPiece != null && targetPiece.getPlayer() == currentPlayer) {
            return false;
        }

        return isValidPieceMove(piece, from, to, targetPiece != null);
    }

    public boolean isValidPieceMove(Piece piece, Position from, Position to, boolean isCapture) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = to.getY() - from.getY();

        return switch (piece.getType()) {
            case KING -> (dx <= 1 && Math.abs(dy) <= 1);
            case QUEEN -> (isStraightMove(from, to) || (isDiagonalMove(from, to)));
            case ROOK -> isStraightMove(from, to);
            case BISHOP -> isDiagonalMove(from, to);
            case KNIGHT -> (dx == 1 && Math.abs(dy) == 2) || (dx == 2 && Math.abs(dy) == 1);
            case PAWN -> isValidPawnMove(piece, from, to, dy, dx, isCapture);
            default -> false;
        };
    }


    private boolean isValidPawnMove(Piece piece, Position from, Position to, int dy, int dx, boolean isCapture) {
        int direction = piece.getPlayer() == Player.WHITE ? 1 : -1;

        if (!isCapture) {
            if (dx != 0) return false; // Pawns can't move sideways without capturing

            // Single move forward
            if (dy == direction) {
                return grid[to.getX()][to.getY()] == null;
            }
            // Double move from starting position
            if (dy == 2 * direction &&
                    ((piece.getPlayer() == Player.WHITE && from.getY() == 1) ||
                            (piece.getPlayer() == Player.BLACK && from.getY() == 6))) {
                return grid[from.getX()][from.getY() + direction] == null &&
                        grid[to.getX()][to.getY()] == null;
            }
            return false;
        }
        // Capture must be exactly 1 square diagonally forward
        return dx == 1 && dy == direction;
    }

    private boolean isStraightMove(Position from, Position to) {
        if (from.getX() == to.getX()) {
            int step = from.getY() < to.getY() ? 1 : -1;
            for (int y = from.getY() + step; y != to.getY(); y += step) {
                if (grid[from.getX()][y] != null) {
                    return false;
                }
            }
            return true;
        } else if (from.getY() == to.getY()) {
            int step = from.getX() < to.getX() ? 1 : -1;
            for (int x = from.getX() + step; x != to.getX(); x += step) {
                if (grid[x][from.getY()] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isDiagonalMove(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        if (Math.abs(dx) != Math.abs(dy)) {
            return false;
        }

        int xStep = dx > 0 ? 1 : -1;
        int yStep = dy > 0 ? 1 : -1;

        for (int x = from.getX() + xStep, y = from.getY() + yStep;
             x != to.getX();
             x += xStep, y += yStep) {
            if (grid[x][y] != null) {
                return false;
            }
        }

        return true;
    }


    public boolean isInCheck(Player player) {
        Position kingPosition = player == Player.WHITE ? whiteKingPosition : blackKingPosition;
        Player opponent = player.opponent();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = grid[x][y];
                if (piece != null && piece.getPlayer() == opponent) {
                    ChessMove hypotheticalMove = new ChessMove(new Position(x, y), kingPosition);
                    if (isValidPieceMove(piece, new Position(x, y), kingPosition, true)) {
                        if (piece.getType() == PieceType.KNIGHT ||
                                (piece.getType() == PieceType.PAWN && Math.abs(x - kingPosition.getX()) == 1) ||
                                isPathClear(new Position(x, y), kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isPathClear(Position from, Position to) {
        if (from.getX() == to.getX() || from.getY() == to.getY()) {
            return isStraightMove(from, to);
        } else {
            return isDiagonalMove(from, to);
        }
    }

    private boolean isValidPosition(Position position) {
        return position.getX() >= 0 && position.getX() < 8 &&
                position.getY() >= 0 && position.getY() < 8;
    }

    // Getters and Setters
    public Piece[][] getGrid() {
        return grid;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getMoveHistory() {
        return moveHistory;
    }

    public void appendMoveHistory(String move) {
        this.moveHistory += move;
    }

    public Position getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public Position getBlackKingPosition() {
        return blackKingPosition;
    }

    public void setGrid(Piece[][] grid) {
        this.grid = grid;
    }

    public void setMoveHistory(String moveHistory) {
        this.moveHistory = moveHistory;
    }

    public void setWhiteKingPosition(Position whiteKingPosition) {
        this.whiteKingPosition = whiteKingPosition;
    }

    public void setBlackKingPosition(Position blackKingPosition) {
            this.blackKingPosition = blackKingPosition;
    }

    public void reset() {
    }
}