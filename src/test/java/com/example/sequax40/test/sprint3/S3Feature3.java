package com.example.sequax40.test.sprint3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.model.move.Move;

public class S3Feature3 {

    private Board board;
    private GameManager manager;
    private Map<String, Tile> tileMap;

    @BeforeEach
    void setup() {
        board = new Board(11, 11);
        tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);
    }

    private Move moveFor(Tile tile) {
        return new Move(tile.getCoord(), tile.getShape());
    }

    private long countTiles(PlayerEnum player) {
        return tileMap.values().stream()
                .filter(t -> t.getOwner() == player)
                .count();
    }

    @Test
    void botFirstMoveIsCentre() {
        Tile centre = tileMap.get("F6");
        assertNotNull(centre);

        boolean move = manager.makeMove(moveFor(centre));

        assertTrue(move);
        assertEquals(PlayerEnum.BLACK, centre.getOwner());
        assertEquals(1, manager.getMoveCount());
    }

    @Test
    void moveCountIncrementsCorrectly() {
        Tile t1 = tileMap.get("F6");
        Tile t2 = tileMap.get("F7");

        manager.makeMove(moveFor(t1));
        manager.makeMove(moveFor(t2));

        assertEquals(2, manager.getMoveCount());
    }

    @Test
    void cannotPlaySameTileTwice() {
        Tile tile = tileMap.get("F6");

        assertTrue(manager.makeMove(moveFor(tile)));
        assertFalse(manager.makeMove(moveFor(tile)));
    }

    @Test
    void turnSwitchesAfterValidMove() {
        Tile t1 = tileMap.get("F6");

        manager.makeMove(moveFor(t1));

        assertEquals(PlayerEnum.WHITE, manager.getCurrentTurn());
    }

    @Test
    void turnDoesNotChangeOnInvalidMove() {
        Tile tile = tileMap.get("F6");

        manager.makeMove(moveFor(tile));

        PlayerEnum before = manager.getCurrentTurn();
        boolean move = manager.makeMove(moveFor(tile));

        assertFalse(move);
        assertEquals(before, manager.getCurrentTurn());
    }

    @Test
    void botAlwaysFindsValidMoveWhenAvailable() {
        int attempts = 0;

        while (!manager.isGameOver() && attempts < 50) {
            boolean movePlayed = false;

            for (Tile tile : tileMap.values()) {
                if (tile.isEmpty()) {
                    movePlayed = manager.makeMove(moveFor(tile));
                    if (movePlayed) break;
                }
            }

            if (!movePlayed) break;
            attempts++;
        }

        assertTrue(attempts > 0);
    }

    @Test
    void noInfiniteLoopWhenNoValidMoves() {
        for (Tile tile : tileMap.values()) {
            tile.setOwner(PlayerEnum.BLACK);
        }

        boolean movePlayed = false;

        for (Tile tile : tileMap.values()) {
            if (tile.isEmpty()) {
                movePlayed = manager.makeMove(moveFor(tile));
                break;
            }
        }

        assertFalse(movePlayed);
    }
}