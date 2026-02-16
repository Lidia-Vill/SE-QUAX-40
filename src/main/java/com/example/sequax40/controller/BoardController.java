package com.example.sequax40.controller;

import com.example.sequax40.model.board.Board;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;

public class BoardController {

    private Board board;

    @FXML
    private GridPane boardGrid;

    public void initialize() {
        board = new Board(5, 5); // example 5x5 board
        // populate GridPane with buttons or tiles
    }

    @FXML
    public void handleCellClick() {
        // update board model and refresh UI
    }
}
