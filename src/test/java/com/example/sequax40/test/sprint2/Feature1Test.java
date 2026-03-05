package com.example.sequax40.test.sprint2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Feature1Test {
	
	//INITIALISING
	
	//initialise all the setups for the board
	private BoardController controller;
    private Board board;
    private GameManager manager;

    
    @FXML private Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;
    
    @BeforeAll
    static void initToolkit() {
    	Platform.startup(()-> {});
    }
	
    //ensure each board section is initialised before each test
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
    
    //GAME MANAGER TESTS
    
    @Test //ensure the first player is black
    void testGameStartsWithBlack() {
    	assertEquals(PlayerEnum.BLACK, manager.getCurrentTurn()); //check the current turn in manager at beginning is set to black
    }

    @Test //after one move, current player should be white 
    void testAlternatingTurns1() {
    	Tile tile1 = board.getTile("A1");
    	
    	manager.makeMove(tile1); //make a move on tile A1
    	
    	assertEquals(PlayerEnum.WHITE, manager.getCurrentTurn()); //check current turn is now white 
    }
    
    @Test //ensure after two moves, the turn returns to black
    void testAlternatingTurns2() {
    	Tile tile1 = board.getTile("A1");
    	Tile tile2 = board.getTile("B1");
    	
    	manager.makeMove(tile1);
    	manager.makeMove(tile2);
    	
    	assertEquals(PlayerEnum.BLACK, manager.getCurrentTurn()); //check current turn has reverted to white 
    }
    
    
    //BOARD CONTROLLER TESTS
    
   
    @Test //at beginning of game the label should display blacks turn in the text, and colour of the text as well as the octagon and rhombus
    void testTurnDisplayStartsBlack() throws Exception{
    	    	
    	runOnFxThreadAndWait(() -> controller.initialize());
        
        Label currentTurnLabel = controller.getTurnLabel();
        assertTrue(currentTurnLabel.getText().contains("BLACK"));
        assertEquals(Color.web("2f2f2f"), turnLabel.getTextFill());
        assertEquals(Color.web("2f2f2f"), turnRhom.getFill());
        assertEquals(Color.web("2f2f2f"), turnOct.getFill());
    }
    
    @Test //confirm after a tile is placed, the symbol for the rhombus turns changes from black to white and back to black after a second click
    void testRhomLabelUpdates() {
    	Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");
        
        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA1)));
        assertEquals(Color.WHITE, turnRhom.getFill());

        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA2)));
        assertEquals(Color.web("2f2f2f"), turnRhom.getFill());

    }
    
    @Test //confirm after a tile is placed, the symbol for the octagon turns changes from black to white and back to black after a second click
    void testOctLabelUpdates() {
    	Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");
        
        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA1)));
        assertEquals(Color.WHITE, turnOct.getFill());

        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA2)));
        assertEquals(Color.web("2f2f2f"), turnOct.getFill());

    }
    
    @Test //confirm after a tile is placed, the colour of text to display turns changes from black to white and back to black after a second click
    void testLabelColorUpdates() {
    	Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");
        
        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA1)));
        assertEquals(Color.WHITE, turnLabel.getTextFill());

        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA2)));
        assertEquals(Color.web("2f2f2f"), turnLabel.getTextFill());

    }
    
    @Test //confirm after a tile is placed, the actual text to display turns changes from black to white and back to black after a second click
    void testLabelTextUpdates() {
    	Tile tileA1 = board.getTile("A1");
        Tile tileA2 = board.getTile("A2");
        
        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA1)));
        assertTrue(turnLabel.getText().contains("WHITE"));

        runOnFxThreadAndWait(() -> controller.handleTileClick(mockClickEvent(tileA2)));
        assertTrue(turnLabel.getText().contains("BLACK"));

    }
    
    
    //HELPER METHODS
    
    //
    private void runOnFxThreadAndWait(Runnable action) {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    // helper function to simulate a MouseEvent for testing
    private MouseEvent mockClickEvent(Tile tile) {
    	
    	Polygon dummy = new Polygon();
    	dummy.setUserData(tile);
    	
        return new MouseEvent(MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0,
                javafx.scene.input.MouseButton.PRIMARY,
                1, false, false, false, false,
                true, false, false, true, false, false, null
        ) {
            @Override
            public Object getSource() {
                return  dummy;
            }
        };
    }
	
}
