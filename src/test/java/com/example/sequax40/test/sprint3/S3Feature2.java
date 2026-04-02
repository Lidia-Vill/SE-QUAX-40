package com.example.sequax40.test.sprint3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

class S3Feature2 {

	//initialise all the setups for the board
	private BoardController controller;
    private Board board;
    private GameManager manager;

    
    @FXML private Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;
    @FXML private Button pieRuleButton;
    
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
    	Button pieRuleButton = new Button();
    	turnLabel = new Label();
    	turnOct = new Polygon();
    	turnRhom = new Polygon();
    	
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
    
	@Test
	void testIfPieRuleAlreadyUsed() {
		controller.setPieRuleUsed(true);
		
		PlayerEnum beforePlayer = manager.getCurrentTurn();
		
		controller.handlePieRule(new ActionEvent());
		
		assertTrue(controller.isPieRuleUsed());
		
		assertEquals(beforePlayer, manager.getCurrentTurn());
		
		
	}
	
	
	@Test
	void testHandlePieRuleIfNoFirstMove() {
		PlayerEnum beforePlayer = manager.getCurrentTurn();
		
		controller.handlePieRule(new ActionEvent());
		
		assertFalse(controller.isPieRuleUsed());
		
		assertEquals(beforePlayer, manager.getCurrentTurn());
	}
	
	@Test
	void testHandlePieRuleSuccess() {
		controller.setPieRuleUsed(false);
		
		Tile tile = board.getTile("A1");
		manager.makeMove(tile);
		
		Tile firstTile = manager.getFirstMoveTile();
		
		controller.handlePieRule(new ActionEvent());
		
		assertTrue(controller.isPieRuleUsed());
		
		assertEquals(PlayerEnum.WHITE, firstTile.getOwner());
		
		assertNotEquals(PlayerEnum.WHITE, manager.getCurrentTurn());
	}
	
	@Test 
	void testButtonHiddenBeforeFirstMove() {
		
		controller.updatePieRuleButtonVisibility();
		
		assertFalse(controller.getPieRuleButton().isVisible());
	}
	
	@Test 
	void testButtonVisible() {
		Tile tile = board.getTile("A1"); //black makes a move 
		manager.makeMove(tile);

		controller.setPieRuleUsed(false); //pie rule hasnt been used 
		
		controller.updatePieRuleButtonVisibility();
		
		assertTrue(controller.getPieRuleButton().isVisible()); //ensure button is visible
	}
	
	@Test 
	void testButtonHiddenAfterUse() {
		manager.setMoveCount(1);
		controller.setPieRuleUsed(true);
		
		
		assertFalse(controller.getPieRuleButton().isVisible());
	}
	
	@Test 
	void testButtonHiddenAfterSecondMove() {
		
		Tile blackTile = board.getTile("A1");
		manager.makeMove(blackTile);

		controller.setPieRuleUsed(false);
		
		controller.updatePieRuleButtonVisibility();
		
		assertTrue(controller.getPieRuleButton().isVisible());
		
			
		Tile whiteTile = board.getTile("B1");
		manager.makeMove(whiteTile);
		
		controller.updatePieRuleButtonVisibility();
		
		assertFalse(controller.getPieRuleButton().isVisible());
	}
}
