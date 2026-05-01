package com.example.sequax40.test.sprint2;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.test.helperMethods.BoardDumps;
import com.example.sequax40.test.helperMethods.HelperMethods;

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
	
	private BoardDumps boardDump;
	private HelperMethods helper;
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
        boardDump = new BoardDumps();
        helper = new HelperMethods();
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
        } catch (IllegalStateException ignored) {}
    }

    @Test
    void testBoardResetClearsBoard() {
        boardDump.loadDump(board, helper.cloneBoardDump(BoardDumps.BLACK_WIN_RESULT));
        board.reset();

        assertTrue(helper.boardsAreEqual(boardDump.dumpBoard(board), BoardDumps.EMPTY_BOARD_SMALL));
    }

    @Test
    void testBoardResetClearsMixedBoard() {
        boardDump.loadDump(board, helper.cloneBoardDump(BoardDumps.MIXED_BOARD));
        board.reset();

        assertTrue(helper.boardsAreEqual(boardDump.dumpBoard(board), BoardDumps.EMPTY_BOARD_SMALL));
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
    

}

