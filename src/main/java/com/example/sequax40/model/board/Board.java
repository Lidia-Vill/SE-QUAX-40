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

        for (int row = 0; row < rows; row++) {
            char rowChar = (char) ('A' + row); // A, B, C...
            for (int col = 1; col <= cols; col++) {
                String id = rowChar + String.valueOf(col); // "A1", "B2"
                tiles.put(id, new Tile(id, ShapeEnum.OCTAGON)); // or your concrete Tile subclass
            }
        }

    }

    public Tile getTile(String coord) {
        return tiles.get(coord);
    }
}
