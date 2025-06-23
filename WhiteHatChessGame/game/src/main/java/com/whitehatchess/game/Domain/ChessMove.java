package com.whitehatchess.game.Domain;

public class ChessMove {
    private final Position from;
    private final Position to;

    public ChessMove(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }
}