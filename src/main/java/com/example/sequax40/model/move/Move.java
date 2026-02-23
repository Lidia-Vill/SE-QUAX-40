package com.example.sequax40.model.move;

import com.example.sequax40.enums.ShapeEnum;

public class Move {

    private final String coord;       // e.g., "A1" or "AB_1_2"
    private final ShapeEnum shape;    // OCTAGON or RHOMBUS

    public Move(String coord, ShapeEnum shape) {
        this.coord = coord;
        this.shape = shape;
    }

    // --- Getter for coordinate ---
    public String getCoord() {
        return coord;
    }

    // --- Getter for shape ---
    public ShapeEnum getShape() {
        return shape;
    }

    @Override
    public String toString() {
        return "Move{" +
                "coord='" + coord + '\'' +
                ", shape=" + shape +
                '}';
    }
}