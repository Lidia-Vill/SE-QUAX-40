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
        if (tile == null) {
            throw new IllegalArgumentException("Tile cannot be null.");
        }

        String coord = tile.getCoord();

        if (coord == null || coord.isBlank()) {
            throw new IllegalArgumentException("Tile coordinate cannot be null or blank.");
        }

        if (!isValidCoordinate(coord)) {
            throw new IllegalArgumentException("Invalid coordinate: " + coord);
        }

        if (tiles.containsKey(coord)) {
            throw new IllegalStateException("Tile already exists at: " + coord);
        }

        // Shape validation
        if (coord.contains("_") && tile.getShape() != ShapeEnum.RHOMBUS) {
            throw new IllegalArgumentException("Rhombus coordinate must have RHOMBUS shape.");
        }

        if (!coord.contains("_") && tile.getShape() != ShapeEnum.OCTAGON) {
            throw new IllegalArgumentException("Octagon coordinate must have OCTAGON shape.");
        }

        tiles.put(coord, tile);
    }

    public Map<String, Tile> getAllTiles() {
        return tiles;
    }


    private boolean isValidCoordinate(String coord) {

        //octagon
        if (!coord.contains("_")) {
            if (coord.length() < 2) return false;

            char letter = coord.charAt(0);
            String numberPart = coord.substring(1);

            if (!Character.isLetter(letter)) return false;

            try {
                int number = Integer.parseInt(numberPart);

                return letter >= 'A'
                        && letter < ('A' + cols)
                        && number >= 1
                        && number <= rows;

            } catch (NumberFormatException e) {
                return false;
            }
        }

        // rhombus
        else {
            String[] parts = coord.split("_");
            if (parts.length != 3) return false;

            String letters = parts[0];
            if (letters.length() != 2) return false;

            char first = letters.charAt(0);
            char second = letters.charAt(1);

            try {
                int lower = Integer.parseInt(parts[1]);
                int upper = Integer.parseInt(parts[2]);

                return first >= 'A'
                        && first < ('A' + cols - 1)
                        && second == first + 1
                        && lower >= 1
                        && upper == lower + 1
                        && upper <= rows;

            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

}
