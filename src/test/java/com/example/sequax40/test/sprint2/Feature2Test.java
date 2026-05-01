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
import com.example.sequax40.model.move.Move;

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

    private static final int[][] VALID_RHOMBUS_DIAG1 = {
            {1,0,0},
            {0,0,0},
            {0,0,1}
    };

    private static final int[][] VALID_RHOMBUS_DIAG2 = {
            {0,0,1},
            {0,0,0},
            {1,0,0}
    };

    private static final int[][] INVALID_RHOMBUS_EMPTY = {
            {0,0,0},
            {0,0,0},
            {0,0,0}
    };

    private static final int[][] INVALID_RHOMBUS_OTHER_OWNER = {
            {2,0,0},
            {0,0,0},
            {0,0,2}
    };

    private static final int[][] OCCUPIED_TILE = {
            {1,0,0},
            {0,0,0},
            {0,0,0}
    };

    @BeforeAll
    static void initJfx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {}
    }

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

    // helper: construct a Move from a Tile
    private Move moveFor(Tile tile) {
        return new Move(tile.getCoord(), tile.getShape());
    }

    private void loadDump(int[][] dump) {
        for (int row = 0; row < dump.length; row++) {
            for (int col = 0; col < dump[row].length; col++) {
                String coord = "" + (char)('A' + col) + (row + 1);
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

    @Test
    void validRhombusDiagonal1() {
        loadDump(VALID_RHOMBUS_DIAG1);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertTrue(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void validRhombusDiagonal2() {
        loadDump(VALID_RHOMBUS_DIAG2);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertTrue(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void invalidRhombusEmptyDiagonal() {
        loadDump(INVALID_RHOMBUS_EMPTY);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertFalse(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void invalidRhombusOtherPlayer() {
        loadDump(INVALID_RHOMBUS_OTHER_OWNER);
        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertFalse(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void cannotPlaceOnOccupiedTile() {
        loadDump(OCCUPIED_TILE);
        Tile tile = board.getTile("A1");

        assertFalse(manager.makeMove(moveFor(tile)));
    }

    @Test
    void staysTurnOnInvalidMove() {
        loadDump(OCCUPIED_TILE);
        Tile tile = board.getTile("A1");

        PlayerEnum before = manager.getCurrentTurn();
        boolean moveMade = manager.makeMove(moveFor(tile));

        assertFalse(moveMade);
        assertEquals(before, manager.getCurrentTurn());
    }
}