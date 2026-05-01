package com.example.sequax40.test.sprint2;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.example.sequax40.test.helperMethods.ControllerHelpers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class S2Feature1Test {

	private ControllerHelpers helper = new ControllerHelpers();
    private BoardController controller;
    private Board board;
    private GameManager manager;

    @FXML private Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;
    @FXML private Button pieRuleButton;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }

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
        pieRuleButton = new Button();

        controller.setMainContainer(mainContainer);
        controller.setWindowContainer(windowContainer);
        controller.setMasterGroup(masterGroup);
        controller.setBoardGroup(boardGroup);
        controller.setTurnLabel(turnLabel);
        controller.setTurnOct(turnOct);
        controller.setTurnRhom(turnRhom);
        controller.setPieRuleButton(pieRuleButton);
        controller.setBoard(board);
        controller.setGameManager(manager);
    }

    // Game Manager Tests
    
    @Test
    void testGameStartsWithBlack() {
        assertEquals(PlayerEnum.BLACK, manager.getCurrentTurn());
    }

    @Test
    void testAlternatingTurns1() {
        Tile tile1 = board.getTile("A1");
        manager.makeMove(moveFor(tile1));
        assertEquals(PlayerEnum.WHITE, manager.getCurrentTurn());
    }

    @Test
    void testAlternatingTurns2() {
        Tile tile1 = board.getTile("A1");
        Tile tile2 = board.getTile("B1");
        manager.makeMove(moveFor(tile1));
        manager.makeMove(moveFor(tile2));
        assertEquals(PlayerEnum.BLACK, manager.getCurrentTurn());
    }

    // Board Controller Tests

    @Test
    void testTurnDisplayStartsBlack() throws Exception {
    	helper.runOnFxThreadAndWait(() -> controller.initialize());
        Label currentTurnLabel = controller.getTurnLabel();
        assertTrue(currentTurnLabel.getText().contains("BLACK"));
        assertEquals(Color.web("#2f2f2f"), turnLabel.getTextFill());
        assertEquals(Color.web("#2f2f2f"), turnRhom.getFill());
        assertEquals(Color.web("#2f2f2f"), turnOct.getFill());
    }

    @Test
    void testRhomLabelUpdates() {
        Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA1)));
        assertEquals(Color.WHITE, turnRhom.getFill());

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA2)));
        assertEquals(Color.web("#2f2f2f"), turnRhom.getFill());
    }

    @Test
    void testOctLabelUpdates() {
        Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA1)));
        assertEquals(Color.WHITE, turnOct.getFill());

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA2)));
        assertEquals(Color.web("#2f2f2f"), turnOct.getFill());
    }

    @Test
    void testLabelColourUpdates() {
        Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA1)));
        assertEquals(Color.WHITE, turnLabel.getTextFill());

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA2)));
        assertEquals(Color.web("#2f2f2f"), turnLabel.getTextFill());
    }

    @Test
    void testLabelTextUpdates() {
        Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA1)));
        assertTrue(turnLabel.getText().contains("WHITE"));

        helper.runOnFxThreadAndWait(() -> controller.handleTileClick(helper.mockClickEvent(tileA2)));
        assertTrue(turnLabel.getText().contains("BLACK"));
    }
    

    // Helper Methods

    
  
        
    private Move moveFor(Tile tile) {
        ShapeEnum shape = tile.getCoord().contains("_") ? ShapeEnum.RHOMBUS : ShapeEnum.OCTAGON;
        return new Move(tile.getCoord(), shape);
    }
}