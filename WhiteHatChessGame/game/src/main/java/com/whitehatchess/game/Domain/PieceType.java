package com.whitehatchess.game.Domain;

public enum PieceType {
    KING('K'), QUEEN('Q'),
    ROOK('R'), BISHOP('B'),
    KNIGHT('N'), PAWN('P');

    private final char symbol;

    PieceType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
