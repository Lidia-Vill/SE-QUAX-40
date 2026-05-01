package com.example.sequax40.test.sprint2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.model.move.Move;
import com.example.sequax40.test.helperMethods.HelperMethods;

import java.util.Map;

public class S2Feature2Test {

    private HelperMethods helper;
    private BoardController controller;
    private Board board;
    private GameManager manager;

    @BeforeEach
    void setup() {
        helper = new HelperMethods();

        controller = helper.createController();
        board = controller.board;
        manager = new GameManager(board, board.getAllTiles());
    }

    private void loadDump(int[][] dump) {
        helper.loadDump(board, board.getAllTiles(), dump);
    }

    private Move moveFor(Tile tile) {
        return new Move(tile.getCoord(), tile.getShape());
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

    // ---------------- DUMPS ----------------

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
}