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

        // initialise octagon tiles
        for (int col = 0; col < cols; col++) { // letters A–K
            char letter = (char) ('A' + col);
            for (int row = 1; row <= rows; row++) { // numbers 1–11
                String id = letter + String.valueOf(row);
                tiles.put(id, new Tile(id, ShapeEnum.OCTAGON));
            }
        }



        // initialise Rhombus Tiles
        for (int letterIndex = 0; letterIndex < cols - 1; letterIndex++) {
            char firstLetter = (char) ('A' + letterIndex);
            char secondLetter = (char) ('A' + letterIndex + 1);

            for (int number = 1; number < rows; number++) {
                int lowerNumber = number;
                int upperNumber = number + 1;

                String id = "" + firstLetter + secondLetter + "_"
                        + lowerNumber + "_" + upperNumber;

                tiles.put(id, new Tile(id, ShapeEnum.RHOMBUS));
            }
        }
    }

    //getters and setters
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
