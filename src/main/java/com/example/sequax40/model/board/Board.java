package com.example.sequax40.model.board;

import java.util.HashMap;
import java.util.Map;

public class Board {

    private final int rows;
    private final int cols;
    private final Map<String, Tile> tiles; // Logical coordinates map

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = new HashMap<>();
    }

    /** Add a tile to the board */
    public void addTile(Tile tile) {
        tiles.put(tile.getCoord(), tile);
    }

    /** Get tile by logical coordinate like "A1" or "DIA_A1_B1" */
    public Tile getTile(String coord) {
        Tile tile = tiles.get(coord);
        if (tile == null) {
            throw new IllegalArgumentException("No tile at coordinate: " + coord);
        }
        return tile;
    }

    /** Toggle selection for a tile by coordinate */
    public boolean toggleTileSelection(String coord) {
        Tile tile = getTile(coord);
        tile.toggleSelected();
        return tile.isSelected();
    }

    /** Reset all tiles */
    public void resetBoard() {
        for (Tile tile : tiles.values()) {
            tile.reset();
        }
    }

    /** Get all tiles */
    public Map<String, Tile> getTiles() {
        return tiles;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
