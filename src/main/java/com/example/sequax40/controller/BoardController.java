package com.example.sequax40.controller;

import com.example.sequax40.model.board.Board;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class BoardController {

    private static final Color SELECTED_COLOR = Color.WHITE;

    private Board board;

    @FXML
    private GridPane boardGrid;

    public void initialize() {
        board = new Board(11, 11);

    }

    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();


        Color originalColor = (Color) clicked.getUserData();


        if (originalColor == null) {
            clicked.setUserData(clicked.getFill());
            clicked.setFill(SELECTED_COLOR);
        } else {

            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor);
            } else {
                clicked.setFill(SELECTED_COLOR);
            }
        }
    }
}