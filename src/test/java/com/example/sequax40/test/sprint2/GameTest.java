
package com.example.sequax40.test.sprint2;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.model.move.Move;
import com.example.sequax40.enums.ShapeEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Board board;
    private GameManager gm;

    // -----------------------
    // STATIC BOARD DUMPS
    // -----------------------

    private static final int[][] EMPTY_BOARD = {
            {0,0,0},
            {0,0,0},
            {0,0,0}
    };

    private static final int[][] BLACK_FIRST_MOVE_RESULT = {
            {3,0,0},
            {0,0,0},
            {0,0,0}
    };

    private static final int[][] BLACK_WIN_SETUP = {
            {3,0,0},
            {3,0,0},
            {0,0,0}
    };

    private static final int[][] BLACK_WIN_RESULT = {
            {3,0,0},
            {3,0,0},
            {3,0,0}
    };

    // -----------------------
    // SETUP
    // -----------------------

    @BeforeEach
    void setup() {
        board = new Board(3,3);
        //gm = new GameManager(board);
    }

    // -----------------------
    // HELPER METHODS
    // -----------------------

    private boolean compare(int[][] a, int[][] b) {

        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }

        return true;
    }

    private int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];

        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }

        return copy;
    }

    // -----------------------
    // QTS TESTS
    // -----------------------
/*
    @Test
    void playerTurnTest() {

        board.loadFromDump(copy(EMPTY_BOARD));

        gm.makeMove(new Move("A1", ShapeEnum.OCTAGON));

        int[][] result = board.dumpBoard();

        assertTrue(compare(result, BLACK_FIRST_MOVE_RESULT));
    }

    @Test
    void invalidMoveTest() {

        board.loadFromDump(copy(BLACK_FIRST_MOVE_RESULT));

        boolean result = gm.makeMove(new Move("A1", ShapeEnum.OCTAGON));

        assertFalse(result);
    }

    @Test
    void winningMoveTest() {

        board.loadFromDump(copy(BLACK_WIN_SETUP));

        gm.makeMove(new Move("C1", ShapeEnum.OCTAGON));

        int[][] result = board.dumpBoard();

        assertTrue(compare(result, BLACK_WIN_RESULT));
    }
*/


    //test the reset board
    @Test
    void boardResetClearsBoard() {

        board.loadFromDump(copy(BLACK_WIN_RESULT));

        board.reset();

        assertTrue(compare(board.dumpBoard(), EMPTY_BOARD));
    }



    private static final int[][] MIXED_BOARD = {
            {3,4,0},
            {0,3,4},
            {4,0,3}
    };

    @Test
    void boardResetClearsMixedBoard() {

        board.loadFromDump(copy(MIXED_BOARD));

        board.reset();

        assertTrue(compare(board.dumpBoard(), EMPTY_BOARD));
    }


    //check it returns to blacks turn after reset
    @Test
    void resetSetsTurnBackToBlack() {

        BoardController controller = new BoardController();
        controller.board = new Board(3,3);

        // simulate some tiles existing
        for (var tile : controller.board.getAllTiles().values()) {
            controller.tileMap.put(tile.getCoord(), tile);
        }

        // reset game
        controller.resetGame();

        // check turn is BLACK again
        assertEquals(PlayerEnum.BLACK, controller.getCurrentTurn());
    }



}
