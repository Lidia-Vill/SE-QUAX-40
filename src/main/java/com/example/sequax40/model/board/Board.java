package com.example.sequax40.model.board;

public class Board {
    private final int rows;
    private final int cols;
    private int[][] cells;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        cells = new int[rows][cols]; // 0 = empty
    }

    public int getCell(int row, int col) { return cells[row][col]; }
    public void setCell(int row, int col, int value) { cells[row][col] = value; }
}
