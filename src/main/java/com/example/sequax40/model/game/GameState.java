package com.example.sequax40.model.game;

import javafx.fxml.FXML;

import com.example.sequax40.model.board.Board;


public class GameState {
    private Board board;
    private String currentPlayer;
    private int turnCount;

    public GameState(int rows, int cols) {
        board = new Board(rows, cols);
        currentPlayer = "Player 1";
        turnCount = 0;
    }

    public Board getBoard() { return board; }
    public String getCurrentPlayer() { return currentPlayer; }
    public void switchPlayer() { currentPlayer = currentPlayer.equals("Player 1") ? "Player 2" : "Player 1"; }
    public int getTurnCount() { return turnCount; }
    public void incrementTurn() { turnCount++; }
}
