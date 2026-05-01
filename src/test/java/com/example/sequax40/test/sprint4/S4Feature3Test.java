package com.example.sequax40.test.sprint4;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import com.example.sequax40.controller.BoardController;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.test.helperMethods.ControllerHelpers;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class S4Feature3Test {
	
	private ControllerHelpers helper;
    private BoardController controller;
    private Board board;
    private GameManager manager;
 
    @FXML private Label timerLabel;
    
	@BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }
	
	@BeforeEach
    void setup() throws Exception {
		helper = new ControllerHelpers();
        controller = helper.createController();
        board = controller.board;
        manager = controller.getGameManager();

        timerLabel = controller.getTimerLabel();

        controller.startTimerForTest();
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
