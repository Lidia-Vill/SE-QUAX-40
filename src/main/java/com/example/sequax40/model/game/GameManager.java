package com.example.sequax40.model.game;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.move.Move;

public class GameManager {
/*
    private final Board board;
    private final GameState gameState;
    private final WinChecker winChecker;

    public GameManager(Board board) {
        this.board = board;
        this.gameState = new GameState(board.getRows(), board.getCols()); // pass rows/cols
        this.winChecker = new WinChecker(board);
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean makeMove(Move move) {
        if (gameState.isGameOver() || move == null) return false;

        Tile tile = board.getTile(move.getCoord());
        if (tile == null || tile.getOwner() != PlayerEnum.EMPTY) return false;

        // Apply move
        tile.setOwner(gameState.getCurrentPlayer());
        tile.setSelected(true); // optional, for UI

        // Check win
        if (winChecker.checkWin(gameState.getCurrentPlayer())) {
            gameState.setWinner(gameState.getCurrentPlayer());
            return true;
        }

        // Increment turn & switch player
        gameState.incrementTurn();
        gameState.switchPlayer();

        return true;
    }*/
}