package com.example.sequax40.test.sprint1;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.test.helperMethods.ControllerHelpers;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

public class S1Feature3Test {
    private BoardController controller;
    private ControllerHelpers helper;
    private Board board;
    private Polygon octagonPolygon;
    private Polygon rhombusPolygon;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        helper = new ControllerHelpers();
        controller = helper.createController();

        octagonPolygon = new Polygon();
        octagonPolygon.setId("A1");

        rhombusPolygon = new Polygon();
        rhombusPolygon.setId("AB_10_11");

        controller.boardGroup.getChildren().addAll(octagonPolygon, rhombusPolygon);

        controller.board = new Board(11, 11);
        controller.setupTiles();

        GameManager gm = new GameManager(controller.board, controller.tileMap);
        controller.setGameManager(gm);

        board = controller.board;
    }

    @Test
    void testOctagonClickTogglesSelection() {
        Tile tile = (Tile) octagonPolygon.getUserData();

        controller.handleTileClick(helper.mockClickEvent(octagonPolygon));

        assertEquals(PlayerEnum.BLACK, tile.getOwner());
        assertEquals(Color.web("#2f2f2f"), octagonPolygon.getFill());
    }

    
    @Test
    void testSetupTiles() {
        controller.board = new Board(11, 11);
        controller.setupTiles();

        Tile octTile = controller.tileMap.get("A1");
        Tile rhombTile = controller.tileMap.get("AB_10_11");

        assertNotNull(octTile, "Octagon tile should exist in tileMap");
        assertNotNull(rhombTile, "Rhombus tile should exist in tileMap");

        assertEquals(ShapeEnum.OCTAGON, octTile.getShape());
        assertEquals(ShapeEnum.RHOMBUS, rhombTile.getShape());

        assertEquals(octTile, controller.polygonMap.get("A1").getUserData());
        assertEquals(rhombTile, controller.polygonMap.get("AB_10_11").getUserData());

        assertEquals(Color.web("#4d44ff"), controller.polygonMap.get("A1").getFill());
        assertEquals(Color.web("#9e9bec"), controller.polygonMap.get("AB_10_11").getFill());
    }

    @Test
    void testHandleTileClickTogglesSelection() {
        controller.board = new Board(11, 11);
        controller.setupTiles();
        GameManager manager = new GameManager(controller.board, controller.tileMap);
        controller.setGameManager(manager);

        Polygon octagonPolygon = controller.polygonMap.get("A1");
        Tile octTile = controller.tileMap.get("A1");

        controller.handleTileClick(helper.mockClickEvent(octagonPolygon));

        assertEquals(PlayerEnum.BLACK, octTile.getOwner());
        assertEquals(Color.web("#2f2f2f"), octagonPolygon.getFill());
    }

    @Test
    void testConstructorShouldThrowExceptionIfCoordIsNull() {
        try {
            new Tile(null, ShapeEnum.OCTAGON);
            fail("Expected IllegalArgumentException for null coordinate");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void testConstructorShouldThrowExceptionIfCoordIsBlank() {
        try {
            new Tile(" ", ShapeEnum.RHOMBUS);
            fail("Expected IllegalArgumentException for blank coordinate");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void testConstructorShouldThrowExceptionIfShapeIsNull() {
        try {
            new Tile("A1", null);
            fail("Expected IllegalArgumentException for null shape");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    void testConstructorShouldCreateTileWithValidInput() {
        Tile tile = new Tile("A1", ShapeEnum.OCTAGON);

        assertEquals("A1", tile.getCoord());
        assertEquals(ShapeEnum.OCTAGON, tile.getShape());
    }

    @Test
    void testTileShouldNotBeSelectedByDefault() {
        Tile tile = new Tile("A1", ShapeEnum.OCTAGON);

        assertFalse(tile.isSelected());
    }

    @Test
    void testToggleSelectedShouldSetSelectedToTrue() {
        Tile tile = new Tile("A1", ShapeEnum.OCTAGON);

        tile.toggleSelected();

        assertTrue(tile.isSelected());
    }

    @Test
    void testToggleSelectedShouldToggleBackToFalse() {
        Tile tile = new Tile("A1", ShapeEnum.OCTAGON);

        tile.toggleSelected();
        tile.toggleSelected();

        assertFalse(tile.isSelected());
    }

    @Test
    void testResetShouldClearSelection() {
        Tile tile = new Tile("A1", ShapeEnum.OCTAGON);

        tile.toggleSelected();
        tile.reset();

        assertFalse(tile.isSelected());
    }

    @Test
    void testAddTileShouldStoreTileCorrectly() {
        Tile tile = new Tile("A1", ShapeEnum.OCTAGON);

        board.addTile(tile);

        Tile result = board.getTile("A1");

        assertNotNull(result);
        assertEquals("A1", result.getCoord());
        assertEquals(ShapeEnum.OCTAGON, result.getShape());
        assertFalse(result.isSelected());
    }

    @Test
    void testAddTileShouldStoreRhombusTile() {
        Tile tile = new Tile("B2", ShapeEnum.RHOMBUS);

        board.addTile(tile);

        Tile result = board.getTile("B2");

        assertNotNull(result);
        assertEquals(ShapeEnum.RHOMBUS, result.getShape());
    }

    @Test
    void testGetTileShouldReturnTileIfExists() {
        Tile tile = new Tile("D4", ShapeEnum.OCTAGON);
        board.addTile(tile);

        Tile result = board.getTile("D4");

        assertNotNull(result);
        assertEquals(tile, result);
    }

    @Test
    void testGetTileShouldReturnNullIfTileDoesNotExist() {
        Tile result = board.getTile("Z9");

        assertNull(result);
    }
    

}
