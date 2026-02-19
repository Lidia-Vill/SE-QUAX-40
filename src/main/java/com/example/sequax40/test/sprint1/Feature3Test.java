package com.example.sequax40.test.sprint1;

import com.example.sequax40.model.board.Tile;
import org.junit.jupiter.api.Test;

public class Feature3Test {

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
