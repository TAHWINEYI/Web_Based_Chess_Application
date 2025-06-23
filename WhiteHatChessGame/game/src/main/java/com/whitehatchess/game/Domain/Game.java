package com.whitehatchess.game.Domain;

import com.whitehatchess.game.Exceptions.InvalidMoveException;
import org.springframework.stereotype.Component;

@Component
public class Game {
    private Board board;
    private Player currentPlayer;
    private boolean isCheck;

    public Game(Board board) {
        this.board = board;  // Fixed: Added missing board initialization
        this.currentPlayer = Player.WHITE;
        this.isCheck = false;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!board.isValidMove(move, currentPlayer)) {
            throw new InvalidMoveException("Invalid move for " + currentPlayer);
        }

        Board testBoard = copyBoard();
        testBoard.applyMove(move);
        if (testBoard.isInCheck(currentPlayer)) {
            throw new InvalidMoveException("Move would leave you in check");
        }

        board.applyMove(move);
        currentPlayer = currentPlayer.opponent();
        isCheck = board.isInCheck(currentPlayer);

        if (isCheck) {
            board.setStatusMessage(currentPlayer + " is in check!");
        } else {
            board.setStatusMessage("Move successful. " + currentPlayer + "'s turn");
        }

        String moveNotation = positionToNotation(move.getFrom()) + positionToNotation(move.getTo());
        board.appendMoveHistory(currentPlayer.opponent() + ": " + moveNotation + "\n");
    }

    private Board copyBoard() {
        Board copy = new Board();
        Piece[][] originalGrid = board.getGrid();
        Piece[][] copiedGrid = new Piece[8][8];

        for (int x = 0; x < 8; x++) {
            System.arraycopy(originalGrid[x], 0, copiedGrid[x], 0, 8);
        }

        copy.setGrid(copiedGrid);
        copy.setWhiteKingPosition(new Position(
                board.getWhiteKingPosition().getX(),
                board.getWhiteKingPosition().getY()));
        copy.setBlackKingPosition(new Position(
                board.getBlackKingPosition().getX(),
                board.getBlackKingPosition().getY()));
        return copy;
    }

    private String positionToNotation(Position position) {
        char file = (char) ('a' + position.getX());
        int rank = position.getY() + 1;
        return "" + file + rank;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheck() {
        return isCheck;
    }
}