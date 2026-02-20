package com.example.sequax40.test.sprint1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

import com.example.sequax40.app.QuaxApplication;
import com.example.sequax40.controller.BoardController;
//had to add --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED & --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
//	to run configurations, to allow testfx to access internal javafx class (had to manually access it) due to encapsulation in newer versions of java fx 

class Feature1Test extends ApplicationTest{

	private Stage stage; //initialise a stage to use for the tests 

    @Override //called by testfx before each test to launch javafx app
    public void start(Stage stage) throws Exception {
        this.stage = stage; 
        new QuaxApplication().start(stage); //start the main application
    }

    @Test
    void testAppLaunchesSuccessfully() {
        assertTrue(stage.isShowing()); //check the stage is visible & window is showing
        assertEquals("Welcome to QUAX-11!", stage.getTitle()); //ensure the title on the board matches the title we made
    }
	
    @Test //make sure the setup process is less than 10 seconds
    void testSetupLessThan10s() {
    	long startTime = System.currentTimeMillis(); //record start time before setup 
        
        BoardController controller = new BoardController(); //create new controller
        controller.masterGroup = new Group(); //initialise javafx components
        controller.mainContainer = new StackPane();
        controller.boardGroup = new Group();
       
        
        controller.initialize();  //run setup 
        
        long endTime = System.currentTimeMillis(); //record time after setup 
        long timeTaken = endTime - startTime; //calculate duration 
        
        assertTrue(timeTaken < 10000, "The setup method took too long: " + timeTaken + "ms");
    }
}
