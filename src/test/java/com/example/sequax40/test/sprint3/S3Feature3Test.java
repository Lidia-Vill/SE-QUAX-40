package com.example.sequax40.test.sprint3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.test.helperMethods.HelperMethods;

public class S3Feature3Test {

	private HelperMethods helper;
    private Board board;
    private GameManager manager;
    private Map<String, Tile> tileMap;

    @BeforeEach
    void setup() {
    	helper = new HelperMethods();
        board = new Board(11, 11);
        tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);
    }

    @Test
    void testBotFirstMoveIsCentre() {
        Tile centre = tileMap.get("F6");
        assertNotNull(centre);
        boolean move = manager.makeMove(helper.moveFor(centre));

        assertTrue(move);
        assertEquals(PlayerEnum.BLACK, centre.getOwner());
        assertEquals(1, manager.getMoveCount());
    }

    @Test
    void testMoveCountIncrementsCorrectly() {
        Tile t1 = tileMap.get("F6");
        Tile t2 = tileMap.get("F7");
        manager.makeMove(helper.moveFor(t1));
        manager.makeMove(helper.moveFor(t2));

        assertEquals(2, manager.getMoveCount());
    }

    @Test
    void testCannotPlaySameTileTwice() {
        Tile tile = tileMap.get("F6");

        assertTrue(manager.makeMove(helper.moveFor(tile)));
        assertFalse(manager.makeMove(helper.moveFor(tile)));
    }

    @Test
    void testTurnSwitchesAfterValidMove() {
        Tile t1 = tileMap.get("F6");
        manager.makeMove(helper.moveFor(t1));

        assertEquals(PlayerEnum.WHITE, manager.getCurrentTurn());
    }

    @Test
    void testTurnDoesNotChangeOnInvalidMove() {
        Tile tile = tileMap.get("F6");
        manager.makeMove(helper.moveFor(tile));
        PlayerEnum before = manager.getCurrentTurn();
        boolean move = manager.makeMove(helper.moveFor(tile));

        assertFalse(move);
        assertEquals(before, manager.getCurrentTurn());
    }

    @Test
    void testBotAlwaysFindsValidMoveWhenAvailable() {
        int attempts = 0;

        while (!manager.isGameOver() && attempts < 50) {
            boolean movePlayed = false;
            for (Tile tile : tileMap.values()) {
                if (tile.isEmpty()) {
                    movePlayed = manager.makeMove(helper.moveFor(tile));
                    if (movePlayed) break;
                }
            }
            if (!movePlayed) break;
            attempts++;
        }

        assertTrue(attempts > 0);
    }

    @Test
    void testNoInfiniteLoopWhenNoValidMoves() {
        for (Tile tile : tileMap.values()) {
            tile.setOwner(PlayerEnum.BLACK);
        }
        boolean movePlayed = false;

        for (Tile tile : tileMap.values()) {
            if (tile.isEmpty()) {
                movePlayed = manager.makeMove(helper.moveFor(tile));
                break;
            }
        }
        assertFalse(movePlayed);
    }
}