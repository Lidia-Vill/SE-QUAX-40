package com.example.sequax40.test.sprint2;



import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;


public class Feature3Test {
    private Board board;

    // -----------------------
    // STATIC BOARD DUMPS
    // -----------------------

    private static final int[][] EMPTY_BOARD = {
            {0,0,0},
            {0,0,0},
            {0,0,0}
    };

    private static final int[][] BLACK_WIN_RESULT = {
            {3,0,0},
            {3,0,0},
            {3,0,0}
    };

    private static final int[][] MIXED_BOARD = {
            {3,4,0},
            {0,3,4},
            {4,0,3}
    };

    // -----------------------
    // SETUP
    // -----------------------

    @BeforeEach
    void setup() {
        board = new Board(3,3);
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

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // already started
        }
    }

    // -----------------------
    // TESTS
    // -----------------------

    // test the reset board
    @Test
    void boardResetClearsBoard() {

        board.loadFromDump(copy(BLACK_WIN_RESULT));

        board.reset();

        assertTrue(compare(board.dumpBoard(), EMPTY_BOARD));
    }


    //check for a mixed board
    @Test
    void boardResetClearsMixedBoard() {

        board.loadFromDump(copy(MIXED_BOARD));

        board.reset();

        assertTrue(compare(board.dumpBoard(), EMPTY_BOARD));
    }

    // check it returns to blacks turn after reset
    @Test
    void resetSetsTurnBackToBlack() {

        BoardController controller = new BoardController();
        controller.board = new Board(3,3);

        // give dummy UI elements
        controller.setTurnLabel(new Label());
        controller.setTurnOct(new Polygon());
        controller.setTurnRhom(new Polygon());

        controller.resetGame();

        assertEquals(PlayerEnum.BLACK, controller.getCurrentTurn());
    }
}
