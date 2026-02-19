package com.example.sequax40.model.board;

import com.example.sequax40.enums.PlayerEnum;

public abstract class Tile {

    public enum ShapeType {
        OCTAGON,
        DIAMOND
    }

    private final String coord;      // e.g., "A1" or "DIA_A1_B1"
    private final ShapeType shape;
    private PlayerEnum owner;
    private boolean selected;

    public Tile(String coord, ShapeType shape) {
        this.coord = coord;
        this.shape = shape;
        this.owner = PlayerEnum.EMPTY; // empty by default
        this.selected = false;
    }

    // --- Owner methods ---
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

    // --- Selection methods ---
    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        selected = !selected;
    }

    // --- Getters ---
    public String getCoord() {
        return coord;
    }

    public ShapeType getShape() {
        return shape;
    }
}
