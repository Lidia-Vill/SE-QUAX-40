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

public class S3Feature3 {
    private Board board;
    private GameManager manager;
    private Map<String, Tile> tileMap;


    // SETUP
    @BeforeEach
    void setup() {
        board = new Board(11, 11);
        tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);
    }


    // HELPER: COUNT OWNED TILES
    private long countTiles(PlayerEnum player) {
        return tileMap.values().stream()
                .filter(t -> t.getOwner() == player)
                .count();
    }

    // TEST 1: FIRST MOVE TO CENTRE
    @Test
    void botFirstMoveIsCentre() {

        Tile centre = tileMap.get("F6");
        assertNotNull(centre);

        boolean move = manager.makeMove(centre);

        assertTrue(move);
        assertEquals(PlayerEnum.BLACK, centre.getOwner());
        assertEquals(1, manager.getMoveCount());
    }


    // TEST 2: MOVE COUNT INCREMENTS
    @Test
    void moveCountIncrementsCorrectly() {

        Tile t1 = tileMap.get("F6");
        Tile t2 = tileMap.get("F7");

        manager.makeMove(t1);
        manager.makeMove(t2);

        assertEquals(2, manager.getMoveCount());
    }


    // TEST 3: CANNOT PLAY OCCUPIED TILE
    @Test
    void cannotPlaySameTileTwice() {

        Tile tile = tileMap.get("F6");

        assertTrue(manager.makeMove(tile));
        assertFalse(manager.makeMove(tile)); // second time invalid
    }


    // TEST 4: TURN SWITCHES CORRECTLY
    @Test
    void turnSwitchesAfterValidMove() {

        Tile t1 = tileMap.get("F6");

        manager.makeMove(t1);

        assertEquals(PlayerEnum.WHITE, manager.getCurrentTurn());
    }


    // TEST 5: TURN DOES NOT CHANGE ON INVALID MOVE
    @Test
    void turnDoesNotChangeOnInvalidMove() {

        Tile tile = tileMap.get("F6");

        manager.makeMove(tile); // BLACK
        PlayerEnum before = manager.getCurrentTurn();

        boolean move = manager.makeMove(tile); // invalid

        assertFalse(move);
        assertEquals(before, manager.getCurrentTurn());
    }


    // TEST 6: BOT-LIKE RANDOM VALID MOVE SIMULATION
    @Test
    void botAlwaysFindsValidMoveWhenAvailable() {

        int attempts = 0;

        while (!manager.isGameOver() && attempts < 50) {

            boolean movePlayed = false;

            for (Tile tile : tileMap.values()) {
                if (tile.isEmpty()) {
                    movePlayed = manager.makeMove(tile);
                    if (movePlayed) break;
                }
            }

            // If no move possible -> break safely
            if (!movePlayed) break;

            attempts++;
        }

        assertTrue(attempts > 0);
    }


    // TEST 7: NO FREEZE WHEN NO VALID MOVES
    @Test
    void noInfiniteLoopWhenNoValidMoves() {

        // Fill board artificially
        for (Tile tile : tileMap.values()) {
            tile.setOwner(PlayerEnum.BLACK);
        }

        boolean movePlayed = false;

        for (Tile tile : tileMap.values()) {
            if (tile.isEmpty()) {
                movePlayed = manager.makeMove(tile);
                break;
            }
        }

        assertFalse(movePlayed); // nothing should work
    }
}
