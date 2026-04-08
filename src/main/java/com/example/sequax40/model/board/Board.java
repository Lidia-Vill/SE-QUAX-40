package com.example.sequax40.model.board;
import com.example.sequax40.enums.PlayerEnum;
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

    public void reset() {
        for (Tile tile : tiles.values()) {
            tile.reset();
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void loadFromDump(int[][] dump) {

        for (int r = 0; r < dump.length; r++) {
            for (int c = 0; c < dump[r].length; c++) {

                char rowChar = (char) ('A' + r);
                String coord = rowChar + String.valueOf(c + 1);

                Tile tile = getTile(coord);
                if (tile == null) continue;

                int value = dump[r][c];

                if (value == 0) {
                    tile.setOwner(PlayerEnum.EMPTY);
                } else if (value == 3) {
                    tile.setOwner(PlayerEnum.BLACK);
                } else if (value == 4) {
                    tile.setOwner(PlayerEnum.WHITE);
                }
            }
        }
    }

    public int[][] dumpBoard() {

        int[][] dump = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                char rowChar = (char) ('A' + r);
                String coord = rowChar + String.valueOf(c + 1);

                Tile tile = getTile(coord);

                if (tile == null) {
                    dump[r][c] = 2;
                } else if (tile.getOwner() == PlayerEnum.BLACK) {
                    dump[r][c] = 3;
                } else if (tile.getOwner() == PlayerEnum.WHITE) {
                    dump[r][c] = 4;
                } else {
                    dump[r][c] = 0;
                }
            }
        }

        return dump;
    }


}