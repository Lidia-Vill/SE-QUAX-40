package com.example.sequax40.model.board;
import com.example.sequax40.enums.ShapeEnum;

import java.util.HashMap;
import java.util.Map;


/*
 * Represents the game board as a map of tile coordinates to Tile Objects
 * Contains both octagon tiles (A1-K11) and rhombus tiles between them 
 */
public class Board {

    private final int rows;
    private final int cols;
    private final Map<String, Tile> tiles;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = new HashMap<>();

        initialiseOctagonTiles();
        initialiseRhombusTiles();
    }
    
    private void initialiseOctagonTiles() {
		for (int col = 0; col < cols; col++) { 
	        char letter = (char) ('A' + col);
	        for (int row = 1; row <= rows; row++) {
	            String id = letter + String.valueOf(row);
	            tiles.put(id, new Tile(id, ShapeEnum.OCTAGON));
	        }
	    }
    }
    
    private void initialiseRhombusTiles() {
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
   
    public void reset() {
    	tiles.values().forEach(Tile::reset);
    }

   
    public Tile getTile(String coord) { return tiles.get(coord); }


    public Map<String, Tile> getAllTiles() { return tiles; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
   
    public void addTile(Tile tile) {
        if (tile != null) tiles.put(tile.getCoord(), tile);
    }

}