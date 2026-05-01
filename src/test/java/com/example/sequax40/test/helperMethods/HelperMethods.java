package com.example.sequax40.test.helperMethods;

import java.util.HashMap;
import java.util.Map;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;

public class HelperMethods {

	public Map<String, Tile> buildFullBoard() {
        Map<String, Tile> map = new HashMap<>();

        for (char col = 'A'; col <= 'K'; col++) {
            for (int row = 1; row <= 11; row++) {
                String coord = "" + col + row;
                map.put(coord, new Tile(coord, ShapeEnum.OCTAGON));
            }
        }

        for (char col = 'A'; col < 'K'; col++) {
            char nextCol = (char)(col + 1);
            for (int row = 1; row < 11; row++) {
                String coord = "" + col + nextCol + "_" + row + "_" + (row + 1);
                map.put(coord, new Tile(coord, ShapeEnum.RHOMBUS));
            }
        }

        return map;
    }

}
