package com.example.sequax40.test.sprint1;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
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

    @Test
    void testInitializeSetsPropertiesAndBindings() {
        // instantiate the controller
        BoardController controller = new BoardController();

        controller.masterGroup = new Group();
        controller.mainContainer = new StackPane(); 
        
        // call initialise method 
        controller.initialize();

        assertTrue(controller.masterGroup.isManaged(), "Group should be managed to consider bounds");
        
        //if container size is 0 - should return 1.0
        controller.mainContainer.resize(0, 0);
        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001);

        //if container size is the size of actual board
        controller.mainContainer.resize(900, 850); 
        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001);
        assertEquals(1.0, controller.masterGroup.getScaleY(), 0.001);

        //make the container half the size of the board 
        controller.mainContainer.resize(450, 425);
        assertEquals(0.5, controller.masterGroup.getScaleX(), 0.001);
        assertEquals(0.5, controller.masterGroup.getScaleY(), 0.001);

        //even with extra wide screen Math.min should size board to still be 1:1 as the x is 
        controller.mainContainer.resize(1800, 850);
        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001); 
        assertEquals(1.0, controller.masterGroup.getScaleY(), 0.001);
    }
}
