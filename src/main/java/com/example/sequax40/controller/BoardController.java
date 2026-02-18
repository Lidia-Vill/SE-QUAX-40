package com.example.sequax40.controller;

import com.example.sequax40.model.board.Board;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class BoardController {

    private static final Color DEFAULT_COLOR = Color.web("#4d44ff");
    private static final Color SELECTED_COLOR = Color.RED;

    private Board board;

    @FXML
    private GridPane boardGrid;


    public void initialize() {
        board = new Board(11, 11); // example 5x5 board
        // populate GridPane with buttons or tiles
    }
    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();

        Boolean selected = (Boolean) clicked.getUserData();

        if (selected == null || !selected) {
            clicked.setFill(SELECTED_COLOR);
            clicked.setUserData(true);
        } else {
            clicked.setFill(DEFAULT_COLOR);
            clicked.setUserData(false);
        }
    }



}
