package com.example.sequax40.test.sprint4;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import com.example.sequax40.controller.BoardController;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
 
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

public class S4Feature3 {
	
	// initialise all the setups for the board
    private BoardController controller;
    private Board board;
    private GameManager manager;
 
    @FXML private Label turnLabel;
    @FXML private Label timerLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;
    @FXML private Button pieRuleButton;
    @FXML private Button stratButton;
	
	@BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }
	
	@BeforeEach
    void setup() throws Exception {
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
        timerLabel = new Label();
        turnOct = new Polygon();
        turnRhom = new Polygon();
        stratButton = new Button();
        //set the timers
        timerLabel = new Label();
 
        controller.setMainContainer(mainContainer);
        controller.setWindowContainer(windowContainer);
        controller.setMasterGroup(masterGroup);
        controller.setBoardGroup(boardGroup);
 
        controller.setTurnLabel(turnLabel);
        controller.setTurnOct(turnOct);
        controller.setTurnRhom(turnRhom);
        controller.setPieRuleButton(pieRuleButton);
        controller.setShowStratButton(stratButton);
        
        controller.setTimerLabel(timerLabel);   
        controller.startTimerForTest();
 
        controller.setBoard(board);
        controller.setGameManager(manager);
 
    }
	
	@Test
	void testTimerLabelInitialisesToZero() {
		assertEquals("00:00", timerLabel.getText());
	}
	
	
	@Test 
	void testTimerLabelFormatSeconds() {
		controller.setElapsedSeconds(5);
		controller.updateTimerLabelForTest();
		
		assertEquals("00:05", timerLabel.getText());
	}
	
	@Test
	void testTimerLabelFormatSecondsAndMinutes() {
		controller.setElapsedSeconds(70); 
		controller.updateTimerLabelForTest();
		
		assertEquals("01:10", timerLabel.getText());
	}
	
	@Test
	void testTimerLabelFormatExactlyOneMinute() {
		controller.setElapsedSeconds(60); 
		controller.updateTimerLabelForTest();
		
		assertEquals("01:00", timerLabel.getText());
	}
	
	@Test
	void testTimerLabelFormatLargeTime() {
		controller.setElapsedSeconds(3540); 
		controller.updateTimerLabelForTest();
		
		assertEquals("59:00", timerLabel.getText());
	}
	
	
	
	
	@Test
	void testElapsedSecondsResetToZeroOnStart() {
		assertEquals(0, controller.getElapsedSeconds());
	}
	
	@Test
	void testTimerIsNotNullAfterStart() {
		assertNotNull(controller.getGameTimer());
	}
	
	@Test
	void testTimerCycleCountIsIndefinite() {
		assertEquals(Animation.INDEFINITE, controller.getGameTimer().getCycleCount());
	}
	
	@Test
	void testStartTimerStopsPreviousTimer() {
		javafx.animation.Timeline firstTimer = controller.getGameTimer();
		
		controller.startTimerForTest();
		
		assertEquals(Animation.Status.STOPPED, firstTimer.getStatus());
	}
	
	
	@Test
	void testStopTimerStopsTimer() {
		controller.stopTimerForTest();
		
		assertEquals(Animation.Status.STOPPED, controller.getGameTimer().getStatus());
	}
	
	
	@Test
	void testResetGameResetsElapsedSeconds() {
		controller.setElapsedSeconds(45);
		
		controller.resetGame();
		
		assertEquals(0, controller.getElapsedSeconds());
	}
	
	@Test
	void testResetGameResetsTimerLabel() {
		controller.setElapsedSeconds(90);
		controller.updateTimerLabelForTest();
		
		controller.resetGame();
		
		assertEquals("00:00", timerLabel.getText());
	}
	
	@Test
	void testResetGameCreatesNewTimer() {
		controller.stopTimerForTest();
		controller.resetGame();
		
		assertNotNull(controller.getGameTimer());
		
		assertEquals(Animation.INDEFINITE, controller.getGameTimer().getCycleCount());
	}
	
}
