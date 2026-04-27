package com.example.sequax40.model.board;


import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;

/*
 * Represents a single tile on the board 
 * A tile has a fixed coordinate and shape, and tracks its current owner
 */
public class Tile {

    private final String coord;     
    private final ShapeEnum shape;
    private PlayerEnum owner;
    private boolean selected;
    private Tile[] rhombusCorners;

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

    public boolean isEmpty() {
        return owner == PlayerEnum.EMPTY;
    }

    public void reset() {
    	this.owner = PlayerEnum.EMPTY;
    	this.selected = false;
    }
    
    public String getCoord() { return coord; }
    public ShapeEnum getShape() { return shape; }
    public PlayerEnum getOwner() { return owner; }
    public boolean isSelected() { return selected; }
    
    public void setOwner(PlayerEnum owner) { this.owner = owner; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public void toggleSelected() { selected = !selected; }
	

    public void setRhombusCorners(Tile... corners) {
        this.rhombusCorners = corners;
    }

    public Tile getOtherCorner(Tile corner) {
        if (rhombusCorners == null) return null;
        for (Tile t : rhombusCorners) {
            if (t != null && !t.equals(corner)) {
                return t;
            }
        }
        return null;
    }

}
