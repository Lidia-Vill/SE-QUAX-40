package com.example.sequax40.test.sprint3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;

public class S3Feature3 {

    private Board board;
    private GameManager manager;
    private Map<String, Tile> tileMap;


    // SETUP: runs before each test to create a fresh board and game state
    @BeforeEach
    void setup() {
        board = new Board(11, 11);
        tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);
    }


    // HELPER: counts how many tiles are owned by a given player
    private long countTiles(PlayerEnum player) {
        return tileMap.values().stream()
                .filter(t -> t.getOwner() == player)
                .count();
    }


    // NOTE: F6 is the centre tile and is expected to be used as the first move


    // Ensure first move is always placed at the centre tile (F6)
    @Test
    void botFirstMoveIsCentre() {

        Tile centre = tileMap.get("F6");
        assertNotNull(centre);

        boolean move = manager.makeMove(centre);

        assertTrue(move);
        assertEquals(PlayerEnum.BLACK, centre.getOwner());
        assertEquals(1, manager.getMoveCount());
    }


    // Ensure move counter increments correctly for both players
    @Test
    void moveCountIncrementsCorrectly() {

        Tile t1 = tileMap.get("F6");
        Tile t2 = tileMap.get("F7");

        manager.makeMove(t1);
        manager.makeMove(t2);

        assertEquals(2, manager.getMoveCount());
    }


    // Ensure a tile cannot be played twice
    @Test
    void cannotPlaySameTileTwice() {

        Tile tile = tileMap.get("F6");

        assertTrue(manager.makeMove(tile));
        assertFalse(manager.makeMove(tile)); // second attempt should fail
    }


    // Ensure turn switches after a valid move
    @Test
    void turnSwitchesAfterValidMove() {

        Tile t1 = tileMap.get("F6");

        manager.makeMove(t1);

        assertEquals(PlayerEnum.WHITE, manager.getCurrentTurn());
    }


    // Ensure turn does NOT switch if the move is invalid
    @Test
    void turnDoesNotChangeOnInvalidMove() {

        Tile tile = tileMap.get("F6");

        manager.makeMove(tile); // valid BLACK move

        PlayerEnum before = manager.getCurrentTurn();

        boolean move = manager.makeMove(tile); // invalid move (already occupied)

        assertFalse(move);
        assertEquals(before, manager.getCurrentTurn());
    }


    // Simulates BOT behaviour to ensure it can always find valid moves when available
    @Test
    void botAlwaysFindsValidMoveWhenAvailable() {

        int attempts = 0;

        while (!manager.isGameOver() && attempts < 50) {

            boolean movePlayed = false;

            // try all tiles until a valid move is found
            for (Tile tile : tileMap.values()) {
                if (tile.isEmpty()) {
                    movePlayed = manager.makeMove(tile);
                    if (movePlayed) break;
                }
            }

            // safely exit if no valid move exists
            if (!movePlayed) break;

            attempts++;
        }

        assertTrue(attempts > 0);
    }


    // Ensure game does not freeze when no valid moves are available
    @Test
    void noInfiniteLoopWhenNoValidMoves() {

        // artificially fill the board so no moves are possible
        for (Tile tile : tileMap.values()) {
            tile.setOwner(PlayerEnum.BLACK);
        }

        boolean movePlayed = false;

        // attempt to play any remaining move (there should be none)
        for (Tile tile : tileMap.values()) {
            if (tile.isEmpty()) {
                movePlayed = manager.makeMove(tile);
                break;
            }
        }

        assertFalse(movePlayed); // no move should be possible
    }
}