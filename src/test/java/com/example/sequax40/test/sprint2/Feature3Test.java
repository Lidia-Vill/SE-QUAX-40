package com.example.sequax40.test.sprint2;



import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class Feature3Test {
    private Board board;
    private BoardController controller;
    private GameManager manager;


    @FXML private Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;

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

    /*@BeforeEach
    void setup() {
        board = new Board(3,3);
    }*/

    @BeforeEach
    void setup() {
        board = new Board(11, 11);
        controller = new BoardController();
        Map<String, Tile> tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);
        StackPane mainContainer = new StackPane();
        HBox windowContainer = new HBox();
        Group masterGroup = new Group();
        Group boardGroup = new Group();
        turnLabel = new Label();
        turnOct = new Polygon();
        turnRhom = new Polygon();

        controller.setMainContainer(mainContainer);
        controller.setWindowContainer(windowContainer);
        controller.setMasterGroup(masterGroup);
        controller.setMasterGroup(boardGroup);

        controller.setTurnLabel(turnLabel);
        controller.setTurnOct(turnOct);
        controller.setTurnRhom(turnRhom);

        controller.setBoard(board);
        controller.setGameManager(manager);

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

       /* BoardController controller = new BoardController();
        Map<String, Tile> tileMap = board.getAllTiles();
        GameManager manager = new GameManager(board, tileMap);
        */
        controller.board = new Board(3,3);

        // give dummy UI elements
        controller.setTurnLabel(new Label());
        controller.setTurnOct(new Polygon());
        controller.setTurnRhom(new Polygon());

        controller.resetGame();

        assertEquals(PlayerEnum.BLACK, manager.getCurrentTurn());
    }
}
