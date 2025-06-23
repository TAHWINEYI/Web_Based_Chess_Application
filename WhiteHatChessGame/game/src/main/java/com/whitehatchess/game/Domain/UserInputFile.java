package com.whitehatchess.game.Domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UserInputFile implements UserInput {
    private final BufferedReader reader;

    public UserInputFile(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
    }

    @Override
    public ChessMove nextMove() throws Exception {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        line = line.trim();
        if (line.length() != 4) {
            throw new IllegalArgumentException("Move must be 4 characters long");
        }

        int fromX = line.charAt(0) - 'a';
        int fromY = Character.getNumericValue(line.charAt(1)) - 1;
        int toX = line.charAt(2) - 'a';
        int toY = Character.getNumericValue(line.charAt(3)) - 1;

        return new ChessMove(new Position(fromX, fromY), new Position(toX, toY));
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}