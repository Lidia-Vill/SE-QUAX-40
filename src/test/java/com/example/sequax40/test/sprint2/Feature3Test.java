package com.example.sequax40.test.sprint2;



import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
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
    @FXML private Button showStrat;


    // board dumps
    // 0 = empty
    // 1 = black (current turn)
    // 2 = white

    //an empty board
    private static final int[][] EMPTY_BOARD = {
            {0,0,0},
            {0,0,0},
            {0,0,0}
    };

    //board with only  black tiles
    private static final int[][] BLACK_WIN_RESULT = {
            {1,0,0},
            {1,0,0},
            {1,0,0}
    };

    //board containing both black and white tiles
    private static final int[][] MIXED_BOARD = {
            {1,2,0},
            {0,1,2},
            {2,0,1}
    };


    // setup - runs before each test so that every test starts with a blank baord
    @BeforeEach
    void setup() {
        board = new Board(3, 3);
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
        showStrat = new Button();

        controller.setMainContainer(mainContainer);
        controller.setWindowContainer(windowContainer);
        controller.setMasterGroup(masterGroup);
        controller.setMasterGroup(boardGroup);

        controller.setTurnLabel(turnLabel);
        controller.setTurnOct(turnOct);
        controller.setTurnRhom(turnRhom);
        controller.setShowStratButton(showStrat);

        controller.setBoard(board);
        controller.setGameManager(manager);

    }


    //this is used by the tests to compare two boards (used after reset to check the board is equal to the expected empty board)
    private boolean compare(int[][] a, int[][] b) {
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }


    //below makes a duplicate of the board so the original isnt modified
    private int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    //javaFX initialization
    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // already started
        }
    }


    // TESTS

    // test the reset board
    @Test
    //check reset clears a board which had a winning black column (contains only black tiles)
    void boardResetClearsBoard() {

        board.loadFromDump(copy(BLACK_WIN_RESULT));

        board.reset();

        assertTrue(compare(board.dumpBoard(), EMPTY_BOARD));
    }


    //check reset works for a mixed board
    @Test
    void boardResetClearsMixedBoard() {

        board.loadFromDump(copy(MIXED_BOARD));

        board.reset();

        assertTrue(compare(board.dumpBoard(), EMPTY_BOARD));
    }

    // check it returns to blacks turn after reset
    @Test
    void resetSetsTurnBackToBlack() {

        controller.board = new Board(3,3);


        controller.setTurnLabel(new Label());
        controller.setTurnOct(new Polygon());
        controller.setTurnRhom(new Polygon());

        controller.resetGame();

        assertEquals(PlayerEnum.BLACK, manager.getCurrentTurn());
    }
}
