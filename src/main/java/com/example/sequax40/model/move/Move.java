package com.example.sequax40.model.move;

import com.example.sequax40.enums.ShapeEnum;

/*
 * Represents a single move made on the board.
 * A move records the coordinates and shape of the tile played.
 */
public class Move {

    private final String coord;       
    private final ShapeEnum shape;    

    public Move(String coord, ShapeEnum shape) {
        this.coord = coord;
        this.shape = shape;
    }

    public String getCoord() { return coord; }
    public ShapeEnum getShape() { return shape; }

    @Override
    public String toString() {
        return "Move{" +  "coord='" + coord + '\'' + ", shape=" + shape + '}';
    }
}