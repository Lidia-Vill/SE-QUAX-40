package com.example.sequax40.model.board;
import com.example.sequax40.enums.ShapeEnum;
import java.util.HashMap;
import java.util.Map;

public class Board {

    private final int rows;
    private final int cols;
    private final Map<String, Tile> tiles;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = new HashMap<>();

        // Initialize octagon tiles
        for (int row = 0; row < rows; row++) {
            char rowChar = (char) ('A' + row); // A, B, C...
            for (int col = 1; col <= cols; col++) {
                String id = rowChar + String.valueOf(col); // e.g., "A1"
                tiles.put(id, new Tile(id, ShapeEnum.OCTAGON));
            }
        }

        // Initialize Rhombus Tiles as well (sprint 2)
        // Example: AB_1_2, BC_2_3 etc.
        String[] rhombusIds = {"A_B_1_2", "BC2_3"}; // placeholder example
        for (String id : rhombusIds) {
            tiles.put(id, new Tile(id, ShapeEnum.RHOMBUS));
        }
    }

    public Tile getTile(String coord) {
        return tiles.get(coord);
    }

    public void addTile(Tile tile) {
        if (tile != null) tiles.put(tile.getCoord(), tile);
    }

    public Map<String, Tile> getAllTiles() {
        return tiles;
    }
}
