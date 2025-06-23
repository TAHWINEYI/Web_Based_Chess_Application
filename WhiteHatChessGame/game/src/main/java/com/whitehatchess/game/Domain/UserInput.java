package com.whitehatchess.game.Domain;

public interface UserInput {
    ChessMove nextMove() throws Exception;
    void close() throws Exception;
}