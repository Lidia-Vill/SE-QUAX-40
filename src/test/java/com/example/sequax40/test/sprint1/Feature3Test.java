package com.example.sequax40.test.sprint1;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;



import static org.junit.jupiter.api.Assertions.*;

public class Feature3Test {

    private BoardController controller;
    private Board board;
    private Polygon octagonPolygon;
    private Polygon rhombusPolygon;

    @BeforeEach
    void setUp() {
        controller = new BoardController();

        // Manually mock FXML-injected fields
        controller.masterGroup = new Group();
        controller.mainContainer = new StackPane();
        controller.boardGroup = new Group();

        // Add some mock polygons
        Polygon octagon = new Polygon();
        octagon.setId("A1");

        Polygon rhombus = new Polygon();
        rhombus.setId("AB10_11");

        controller.boardGroup.getChildren().addAll(octagon, rhombus);
    }


    @Test
    void testOctagonClickTogglesSelection() {
        Tile tile = (Tile) octagonPolygon.getUserData();

        // First click
        controller.handleTileClick(mockClickEvent(octagonPolygon));
        assertTrue(tile.isSelected(), "Octagon tile should be selected");
        assertEquals(Color.WHITE, octagonPolygon.getFill());

        // Second click
        controller.handleTileClick(mockClickEvent(octagonPolygon));
        assertFalse(tile.isSelected(), "Octagon tile should be unselected");
        assertEquals(Color.web("#4d44ff"), octagonPolygon.getFill());
    }

    @Test
    void testRhombusClickTogglesSelection() {
        Tile tile = (Tile) rhombusPolygon.getUserData();

        // First click
        controller.handleTileClick(mockClickEvent(rhombusPolygon));
        assertTrue(tile.isSelected(), "Rhombus tile should be selected");
        assertEquals(Color.WHITE, rhombusPolygon.getFill());

        // Second click
        controller.handleTileClick(mockClickEvent(rhombusPolygon));
        assertFalse(tile.isSelected(), "Rhombus tile should be unselected");
        assertEquals(Color.web("#9e9bec"), rhombusPolygon.getFill());
    }

    // Helper to simulate a MouseEvent for testing
    private MouseEvent mockClickEvent(Polygon polygon) {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0,
                javafx.scene.input.MouseButton.PRIMARY,
                1, false, false, false, false,
                true, false, false, true, false, false, null
        ) {
            @Override
            public Object getSource() {
                return  polygon;
            }
        };
    }

    @Test
    void testSetupTiles() {
        // Call setupTiles directly
        controller.board = new Board(11, 11);
        controller.setupTiles();

        // Verify tileMap
        Tile octTile = controller.tileMap.get("A1");
        Tile rhombTile = controller.tileMap.get("AB10_11");

        assertNotNull(octTile, "Octagon tile should exist in tileMap");
        assertNotNull(rhombTile, "Rhombus tile should exist in tileMap");

        // Verify shapes
        assertEquals(ShapeEnum.OCTAGON, octTile.getShape());
        assertEquals(ShapeEnum.RHOMBUS, rhombTile.getShape());

        // Verify polygonMap
        assertEquals(octTile, controller.polygonMap.get("A1").getUserData());
        assertEquals(rhombTile, controller.polygonMap.get("AB10_11").getUserData());

        // Verify default fill color
        assertEquals(Color.web("#4d44ff"), controller.polygonMap.get("A1").getFill());
        assertEquals(Color.web("#4d44ff"), controller.polygonMap.get("AB10_11").getFill());
    }

    @Test
    void testHandleTileClickTogglesSelection() {
        controller.board = new Board(11, 11);
        controller.setupTiles();

        Polygon octagonPolygon = controller.polygonMap.get("A1");
        Tile octTile = controller.tileMap.get("A1");

        // First click
        controller.handleTileClick(mockClickEvent(octagonPolygon));

        assertTrue(octTile.isSelected());
        assertEquals(Color.WHITE, octagonPolygon.getFill());

        // Second click
        controller.handleTileClick(mockClickEvent(octagonPolygon));

        assertFalse(octTile.isSelected());
        assertEquals(Color.web("#4d44ff"), octagonPolygon.getFill());
    }

    @Test
    void testInitialize() {
        // Setup a fresh controller with mocked UI
        controller.initialize();

        assertNotNull(controller.board, "Board should be initialized");
        assertFalse(controller.tileMap.isEmpty(), "tileMap should be populated");
        assertFalse(controller.polygonMap.isEmpty(), "polygonMap should be populated");

        // Verify at least one polygon has Tile in userData
        Polygon polygon = controller.polygonMap.values().iterator().next();
        assertNotNull(polygon.getUserData());
        assertTrue(polygon.getUserData() instanceof Tile);
    }


}
