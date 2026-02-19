package com.example.sequax40.test.sprint1;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Feature3Test {

    private Board board;

    @BeforeEach
    void setUp() {
        // Create an 11x11 board for testing
        board = new Board(11, 11);
    }

    @Test
    void testTwoTilesClickable() {
        Tile tile1 = board.getTile("A1");
        Tile tile2 = board.getTile("B2");

        // Initially not selected
        assertFalse(tile1.isSelected());
        assertFalse(tile2.isSelected());

        // Simulate clicks
        tile1.toggleSelected();
        tile2.toggleSelected();

        // Check that the internal state updates
        assertTrue(tile1.isSelected());
        assertTrue(tile2.isSelected());

        // Simulate unclick
        tile1.toggleSelected();
        assertFalse(tile1.isSelected());
    }
}
