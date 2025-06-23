package com.whitehatchess.game.Domain;

public class Piece {
    private final PieceType type;
    private final Player player;
    private boolean hasMoved = false;
    private Position position;

    public Piece(PieceType type, Player player) {
        this.type = type;
        this.player = player;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public char setPosition(Position position) {
        this.position = position;
        return 0;
    }

    public Player getPlayer() {
        return player;
    }

    public PieceType getType() {
        return type;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }

    public String toEmoji() {
        return switch (player) {
            case WHITE -> switch (type) {
                case KING -> "♔";
                case QUEEN -> "♕";
                case ROOK -> "♖";
                case BISHOP -> "♗";
                case KNIGHT -> "♘";
                case PAWN -> "♙";
            };
            case BLACK -> switch (type) {
                case KING -> "♚";
                case QUEEN -> "♛";
                case ROOK -> "♜";
                case BISHOP -> "♝";
                case KNIGHT -> "♞";
                case PAWN -> "♟";
            };
        };
    }
}
