package com.whitehatchess.game.Domain;

public enum Player {
    WHITE(1), BLACK(2);

    private final int value;

    Player(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Player opponent() {
        return this == WHITE ? BLACK : WHITE;
    }
}
