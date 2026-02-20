package com.example.sequax40.test.sprint1;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class Feature2Test {

    // initialise the JavaFX Toolkit before running any tests
    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }
    
    //set up a board to test initialisation 
    private BoardController controller;
    private Board board;
    private Polygon octagonPolygon;
    private Polygon rhombusPolygon;
    
    //set up method for creating tiles to check
    @BeforeEach
    void setUp() {
        controller = new BoardController();

        // Mock FXML-injected fields
        controller.masterGroup = new Group();
        controller.mainContainer = new StackPane();
        controller.boardGroup = new Group();

        // assign to polygon class field and give the tile an id 
        octagonPolygon = new Polygon();
        octagonPolygon.setId("A1");

        rhombusPolygon = new Polygon();
        rhombusPolygon.setId("AB10_11");

        controller.boardGroup.getChildren().addAll(octagonPolygon, rhombusPolygon);

        // create board and setup tiles so userData is set
        controller.board = new Board(11, 11);
        controller.setupTiles();
    }

    @Test
    void testInitialiseSetsPropertiesAndBindings() {
        
        
        // call initialise method 
        controller.initialize();

        assertTrue(controller.masterGroup.isManaged(), "Group should be managed to consider bounds");
        
        //if container size is 0 - should return 1.0
        controller.mainContainer.resize(0, 0);
        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001);

        //if container size is the size of actual board
        controller.mainContainer.resize(845, 845); 
        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001);
        assertEquals(1.0, controller.masterGroup.getScaleY(), 0.001);

        //make the container half the size of the board 
        controller.mainContainer.resize(422.5, 422.5);
        assertEquals(0.5, controller.masterGroup.getScaleX(), 0.001);
        assertEquals(0.5, controller.masterGroup.getScaleY(), 0.001);

        //even with extra wide screen Math.min should size board to still be 1:1 as the x is 
        controller.mainContainer.resize(1800, 845);
        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001); 
        assertEquals(1.0, controller.masterGroup.getScaleY(), 0.001);
    }
    
    
    
    @Test
    void testInitialise() {
    	
    	// setup a fresh controller with mocked UI
        controller.initialize();

        assertNotNull(controller.board, "Board should be initialised");
        assertFalse(controller.tileMap.isEmpty(), "tileMap should be populated");
        assertFalse(controller.polygonMap.isEmpty(), "polygonMap should be populated");

        // verify at least one polygon has Tile in userData
        Polygon polygon = controller.polygonMap.values().iterator().next();
        assertNotNull(polygon.getUserData());
        assertTrue(polygon.getUserData() instanceof Tile);
    }
}

