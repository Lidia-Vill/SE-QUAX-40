package com.example.sequax40.model.game;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;

/*
 * Holds the state of a game session: the board, whose turn it is,
 * the winner, and whether the game is over
 * Black always starts
 */
public class GameState {

    private final Board board;

    private PlayerEnum currentPlayer;
    private PlayerEnum winner;
    private boolean gameOver;
    private int turnCount;

    public GameState(int rows, int cols) {
        this.board = new Board(rows, cols);
        this.currentPlayer = PlayerEnum.BLACK; 
    }
    
    public void switchPlayer() {
        if (gameOver) return;
        currentPlayer = (currentPlayer == PlayerEnum.BLACK)
                ? PlayerEnum.WHITE
                : PlayerEnum.BLACK;
    }
    
    public void incrementTurn() {
        if (!gameOver) {
            turnCount++;
        }
    }
    
    public void setWinner(PlayerEnum winner) {
        this.winner = winner;
        this.gameOver = true;
    }


    public Board getBoard() { return board; }
    public PlayerEnum getCurrentPlayer() { return currentPlayer; }
    public PlayerEnum getWinner() { return winner; }
    public int getTurnCount() { return turnCount; }
    public boolean isGameOver() { return gameOver; }

}