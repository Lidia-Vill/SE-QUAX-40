package com.example.sequax40.model.board;


import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;

public class Tile {

    private final String coord;      // e.g., "A1" or "AB_1_2"
    private final ShapeEnum shape;
    private PlayerEnum owner;
    private boolean selected;

    //Tile constructor 
    public Tile(String coord, ShapeEnum shape) {
        if (coord == null || coord.isBlank()) {
            throw new IllegalArgumentException("Coordinate cannot be null or blank");
        }

        if (shape == null) {
            throw new IllegalArgumentException("Shape cannot be null");
        }

        this.coord = coord;
        this.shape = shape;
        this.owner = PlayerEnum.EMPTY;
        this.selected = false;
    }

    // owner methods (sprint 2)
    public boolean isEmpty() {
        return owner == PlayerEnum.EMPTY;
    }

    public PlayerEnum getOwner() {
        return owner;
    }

    public void setOwner(PlayerEnum owner) {
        this.owner = owner;
    }

    public void reset() {
        this.owner = PlayerEnum.EMPTY;
        this.selected = false;
    }

    // selection methods
    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        selected = !selected;
    }

    // getters
    public String getCoord() {
        return coord;
    }

    public ShapeEnum getShape() {
        return shape;
    }
}
