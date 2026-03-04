package com.example.sequax40.model.game;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;

public class GameState {

    private final Board board;

    private PlayerEnum currentPlayer;
    private PlayerEnum winner;

    private boolean gameOver;
    private int turnCount;

    public GameState(int rows, int cols) {
        this.board = new Board(rows, cols);
        this.currentPlayer = PlayerEnum.BLACK; // Black starts (standard for connection games)
        this.winner = null;
        this.gameOver = false;
        this.turnCount = 0;
    }

    // --- Board ---
    public Board getBoard() {
        return board;
    }

    // --- Current Player ---
    public PlayerEnum getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        if (gameOver) return;

        currentPlayer = (currentPlayer == PlayerEnum.BLACK)
                ? PlayerEnum.WHITE
                : PlayerEnum.BLACK;
    }

    // --- Turn Count ---
    public int getTurnCount() {
        return turnCount;
    }

    public void incrementTurn() {
        if (!gameOver) {
            turnCount++;
        }
    }

    // --- Game Status ---
    public boolean isGameOver() {
        return gameOver;
    }

    public PlayerEnum getWinner() {
        return winner;
    }

    public void setWinner(PlayerEnum winner) {
        this.winner = winner;
        this.gameOver = true;
    }
/*
    // --- Reset ---
    public void reset() {
        board.reset();
        currentPlayer = PlayerEnum.BLACK;
        winner = null;
        gameOver = false;
        turnCount = 0;
    }

 */
}