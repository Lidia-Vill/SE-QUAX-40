package com.example.sequax40.test.sprint1;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class Feature2Test {

    // Initialise the JavaFX Toolkit before running any tests
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

   
    
    private BoardController createController() {

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

        controller.initialize();

        return controller;
    }
    
    @Test
    void testScalingZeroContainerReturnsOne() {

        BoardController controller = createController();

        controller.mainContainer.resize(0, 0);
        controller.mainContainer.layout();

        assertEquals(1.0, controller.windowContainer.getScaleX(), 0.001);
    }

    @Test
    void testScalingAtDesignSize() {

        BoardController controller = createController();

        controller.mainContainer.resize(900, 850);
        controller.mainContainer.layout();

        assertEquals(0.8181818, controller.windowContainer.getScaleX(), 0.001);
        assertEquals(0.8181818, controller.windowContainer.getScaleY(), 0.001);
    }

    @Test
    void testScalingHalfSize() {

        BoardController controller = createController();

        controller.mainContainer.resize(450, 425);
        controller.mainContainer.layout();
        
        assertEquals(0.4090909, controller.windowContainer.getScaleX(), 0.001);
        assertEquals(0.4090909, controller.windowContainer.getScaleY(), 0.001);
    }

    @Test
    void testScalingExtraWideWindow() {

        BoardController controller = createController();

        controller.mainContainer.resize(1800, 850);
        controller.mainContainer.layout();

        assertEquals(1.0059171, controller.windowContainer.getScaleX(), 0.001);
        assertEquals(1.0059171, controller.windowContainer.getScaleY(), 0.001);
    }
}