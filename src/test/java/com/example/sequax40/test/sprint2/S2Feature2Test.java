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
import com.example.sequax40.test.helperMethods.ControllerHelpers;
import com.example.sequax40.test.helperMethods.BoardDumps;


public class S2Feature2Test {

	private ControllerHelpers helper;
    private BoardDumps boardDumps;
    private BoardController controller;
    private Board board;
    private GameManager manager;

    @BeforeEach
    void setup() {
    	helper = new ControllerHelpers();
        boardDumps = new BoardDumps();

        controller = helper.createController();
        board = controller.board;
        manager = new GameManager(board, board.getAllTiles());
    }

    private void loadDump(int[][] dump) {
        boardDumps.loadDump(board, dump);
    }

    private Move moveFor(Tile tile) {
        return new Move(tile.getCoord(), tile.getShape());
    }

    @Test
    void validRhombusDiagonal1() {
        loadDump(BoardDumps.VALID_RHOMBUS_DIAG1);

        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertTrue(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void validRhombusDiagonal2() {
        loadDump(BoardDumps.VALID_RHOMBUS_DIAG2);

        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertTrue(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void invalidRhombusEmptyDiagonal() {
        loadDump(BoardDumps.INVALID_RHOMBUS_EMPTY);

        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertFalse(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void invalidRhombusOtherPlayer() {
        loadDump(BoardDumps.INVALID_RHOMBUS_OTHER_OWNER);

        Tile rhombus = new Tile("AC_1_3", ShapeEnum.RHOMBUS);
        board.addTile(rhombus);

        assertFalse(manager.makeMove(moveFor(rhombus)));
    }

    @Test
    void cannotPlaceOnOccupiedTile() {
        loadDump(BoardDumps.OCCUPIED_TILE);

        Tile tile = board.getTile("A1");

        assertFalse(manager.makeMove(moveFor(tile)));
    }

    @Test
    void staysTurnOnInvalidMove() {
        loadDump(BoardDumps.OCCUPIED_TILE);

        Tile tile = board.getTile("A1");

        PlayerEnum before = manager.getCurrentTurn();
        boolean moveMade = manager.makeMove(moveFor(tile));

        assertFalse(moveMade);
        assertEquals(before, manager.getCurrentTurn());
    }

}