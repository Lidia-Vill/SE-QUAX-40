package com.example.sequax40.test.sprint1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

import com.example.sequax40.app.QuaxApplication;
import com.example.sequax40.controller.BoardController;
// paste this line below

//  --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

//	into run configurations, to allow testfx to access internal javafx class (have to manually access it)
//	due to encapsulation in newer versions of java fx

class Feature1Test extends ApplicationTest{

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        new QuaxApplication().start(stage);
    }

    @Test
    void testAppLaunchesSuccessfully() {
        assertTrue(stage.isShowing());
        assertEquals("Welcome to QUAX-11!", stage.getTitle());
    }

    @Test
    void testSetupLessThan10s() {
        long startTime = System.currentTimeMillis();

        BoardController controller = new BoardController();

        StackPane mainContainer = new StackPane();
        HBox windowContainer = new HBox();
        Group masterGroup = new Group();
        Group boardGroup = new Group();

        controller.setMainContainer(mainContainer);
        controller.setWindowContainer(windowContainer);
        controller.setMasterGroup(masterGroup);
        controller.setBoardGroup(boardGroup);

        controller.setTurnLabel(new Label());
        controller.setTurnOct(new Polygon());
        controller.setTurnRhom(new Polygon());
        controller.setPieRuleButton(new Button());
        controller.setTimerLabel(new Label());
        controller.setShowStratButton(new Button());
        controller.setStrategyLabel1(new Label());
        controller.setStrategyLabel2(new Label());

        controller.initialize();

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        assertTrue(timeTaken < 10000, "The setup method took too long: " + timeTaken + "ms");
    }
}
