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


public class S2Feature3Test {
    private Board board;
    private BoardController controller;
    private GameManager manager;


    @FXML private Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;
    @FXML private Button showStrat;

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

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // already started
        }
    }

    @Test
    void testBoardResetClearsBoard() {
        board.loadFromDump(cloneBoardDump(BLACK_WIN_RESULT));
        board.reset();

        assertTrue(boardsAreEqual(board.dumpBoard(), EMPTY_BOARD));
    }

    @Test
    void testboardResetClearsMixedBoard() {
        board.loadFromDump(cloneBoardDump(MIXED_BOARD));
        board.reset();

        assertTrue(boardsAreEqual(board.dumpBoard(), EMPTY_BOARD));
    }

    @Test
    void testResetSetsTurnBackToBlack() {
        controller.board = new Board(3,3);
        controller.setTurnLabel(new Label());
        controller.setTurnOct(new Polygon());
        controller.setTurnRhom(new Polygon());

        controller.resetGame();

        assertEquals(PlayerEnum.BLACK, manager.getCurrentTurn());
    }
    
    
    // Helper Methods 
    
    private boolean boardsAreEqual(int[][] a, int[][] b) {
        if (a.length != b.length) return false;

        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a[row].length; col++) {
                if (a[row][col] != b[row][col]) return false;
            }
        }
        return true;
    }


    private int[][] cloneBoardDump(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int row = 0; row < original.length; row++) {
            System.arraycopy(original[row], 0, copy[row], 0, original[row].length);
        }
        return copy;
    }
    
    // Board Dumps
    
    // 0 = empty
    // 1 = black (current turn)
    // 2 = white

    private static final int[][] EMPTY_BOARD = {
            {0,0,0},
            {0,0,0},
            {0,0,0}
    };

    private static final int[][] BLACK_WIN_RESULT = {
            {1,0,0},
            {1,0,0},
            {1,0,0}
    };

    private static final int[][] MIXED_BOARD = {
            {1,2,0},
            {0,1,2},
            {2,0,1}
    };
}

