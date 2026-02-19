package com.example.sequax40.test.sprint1;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Feature3Test {

    private BoardController controller;
    private Board board;
    private Polygon tilePolygon;

    @BeforeEach
    void setUp() {
        controller = new BoardController();

        // Initialize board manually (since FXML not loaded in tests)
        board = new Board(11, 11);
        controller.board = board;

        // Mock a Polygon representing a tile, fx:id = "A1"
        tilePolygon = new Polygon();
        tilePolygon.setId("A1");

        // Link Tile to Polygon
        Tile tile = board.getTile("A1");
        tilePolygon.setUserData(tile);

        // Add to controller tileMap
        controller.tileMap.put("A1", tilePolygon);
    }

    @Test
    void testTileClickTogglesSelection() {
        Tile tile = board.getTile("A1");

        // First click
        controller.handleTileClick(mockMouseEvent(tilePolygon));
        assertTrue(tile.isSelected());
        assertEquals(Color.WHITE, tilePolygon.getFill());

        // Second click
        controller.handleTileClick(mockMouseEvent(tilePolygon));
        assertFalse(tile.isSelected());
        assertEquals(Color.web("#4d44ff"), tilePolygon.getFill());
    }

    @Test
    void testMultipleTilesClickable() {
        Polygon tilePolygon2 = new Polygon();
        tilePolygon2.setId("B2");
        Tile tile2 = board.getTile("B2");
        tilePolygon2.setUserData(tile2);
        controller.tileMap.put("B2", tilePolygon2);

        // Click first tile
        controller.handleTileClick(mockMouseEvent(tilePolygon));
        assertTrue(board.getTile("A1").isSelected());
        assertEquals(Color.WHITE, tilePolygon.getFill());

        // Click second tile
        controller.handleTileClick(mockMouseEvent(tilePolygon2));
        assertTrue(board.getTile("B2").isSelected());
        assertEquals(Color.WHITE, tilePolygon2.getFill());
    }

    private MouseEvent mockMouseEvent(Polygon polygon) {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0,
                MouseButton.PRIMARY, 1,
                false, false, false, false,
                true, false, false, true, false, false, null) {
            @Override
            public Object getSource() {
                return polygon;
            }
        };
    }
}
