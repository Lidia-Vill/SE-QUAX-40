package com.example.sequax40.test.sprint2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

public class Feature2Test {

    private Board board;
    private BoardController controller;
    private GameManager manager;

    private Label turnLabel;
    private Polygon turnOct;
    private Polygon turnRhom;

    // -----------------------
    // BOARD DUMPS
    // -----------------------
    // 0 = EMPTY
    // 1 = BLACK (current turn)
    // 2 = WHITE
    private static final int[][] VALID_RHOMBUS_DIAG1 = {
            {1,0,0},  // diagonal top-left → bottom-right
            {0,0,0},
            {0,0,1}
    };

    private static final int[][] VALID_RHOMBUS_DIAG2 = {
            {0,0,1},  // diagonal top-right → bottom-left
            {0,0,0},
            {1,0,0}
    };

    private static final int[][] INVALID_RHOMBUS_EMPTY = {
            {0,0,0},
            {0,0,0},
            {0,0,0}
    };

    private static final int[][] INVALID_RHOMBUS_OTHER_OWNER = {
            {2,0,0},  // WHITE owns diagonal
            {0,0,0},
            {0,0,2}
    };

    private static final int[][] OCCUPIED_TILE = {
            {1,0,0},  // A1 already has BLACK
            {0,0,0},
            {0,0,0}
    };

    // -----------------------
    // INITIALIZE JAVAFX
    // -----------------------
    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {}
    }

    // -----------------------
    // SETUP
    // -----------------------
    @BeforeEach
    void setup() {
        board = new Board(3,3);
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
    // HELPER: load dump into board
    // -----------------------
    private void loadDump(int[][] dump) {
        for (int row = 0; row < dump.length; row++) {
            for (int col = 0; col < dump[row].length; col++) {
                String coord = "" + (char)('A'+col) + (row+1);
                PlayerEnum owner = switch (dump[row][col]) {
                    case 1 -> PlayerEnum.BLACK;
                    case 2 -> PlayerEnum.WHITE;
                    default -> PlayerEnum.EMPTY;
                };
                Tile tile = new Tile(coord, ShapeEnum.OCTAGON);
                tile.setOwner(owner);
                board.addTile(tile);
            }
        }
    }


    // RHOMBUS TESTS USING DUMPS
    @Test
    void validRhombusDiagonal1() {
        loadDump(VALID_RHOMBUS_DIAG1);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        boolean moveMade = manager.makeMove(rhombus);
        assertTrue(moveMade);
    }

    @Test
    void validRhombusDiagonal2() {
        loadDump(VALID_RHOMBUS_DIAG2);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        boolean moveMade = manager.makeMove(rhombus);
        assertTrue(moveMade);
    }

    @Test
    void invalidRhombusEmptyDiagonal() {
        loadDump(INVALID_RHOMBUS_EMPTY);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        boolean moveMade = manager.makeMove(rhombus);
        assertFalse(moveMade);
    }

    @Test
    void invalidRhombusOtherPlayer() {
        loadDump(INVALID_RHOMBUS_OTHER_OWNER);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        boolean moveMade = manager.makeMove(rhombus);
        assertFalse(moveMade);
    }


    // OCCUPIED TILE TESTS USING DUMPS
    @Test
    void cannotPlaceOnOccupiedTile() {
        loadDump(OCCUPIED_TILE);
        Tile tile = board.getTile("A1");

        boolean moveMade = manager.makeMove(tile);
        assertFalse(moveMade);
    }

    @Test
    void staysTurnOnInvalidMove() {
        loadDump(OCCUPIED_TILE);
        Tile tile = board.getTile("A1");

        PlayerEnum before = manager.getCurrentTurn();
        boolean moveMade = manager.makeMove(tile);

        assertFalse(moveMade);
        assertEquals(before, manager.getCurrentTurn());
    }
}