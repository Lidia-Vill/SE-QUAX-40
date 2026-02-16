package com.example.sequax40.model.board;

import com.example.sequax40.enums.PlayerEnum;

public class Tile {
    private PlayerEnum owner;
    private final int row;
    private final int col;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
        this.owner = PlayerEnum.EMPTY; // empty by default
    }

    // Check if the tile is empty
    public boolean isEmpty() {
        return owner == PlayerEnum.EMPTY;
    }

    // Set the owner of the tile
    public void setOwner(PlayerEnum owner) {
        this.owner = owner;
    }

    // Get the owner of the tile
    public PlayerEnum getOwner() {
        return owner;
    }

    // Get tile position
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // Optional: reset tile to empty
    public void reset() {
        owner = PlayerEnum.EMPTY;
    }
}
