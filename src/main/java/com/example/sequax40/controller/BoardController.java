package com.example.sequax40.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import com.example.sequax40.model.board.Board;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;

public class BoardController { 

    @FXML private StackPane gameBoardStackPane;
    @FXML private Group boardGroup;
    
    private static final Color SELECTED_COLOR = Color.WHITE;


    @FXML private StackPane mainContainer; 
    @FXML private Group masterGroup;      

    private final double DESIGN_WIDTH = 900.0; 
    private final double DESIGN_HEIGHT = 850.0;

    @FXML
    public void initialize() {

        masterGroup.setManaged(true); 
        masterGroup.setTranslateY(-90);

        StackPane.setAlignment(masterGroup, javafx.geometry.Pos.TOP_CENTER);

        NumberBinding scaleBinding = Bindings.createDoubleBinding(() -> {
            double containerWidth = mainContainer.getWidth();
            double containerHeight = mainContainer.getHeight();
            
            if (containerWidth <= 0 || containerHeight <= 0) return 1.0;

            double scaleX = containerWidth / DESIGN_WIDTH;
            double scaleY = containerHeight / DESIGN_HEIGHT;

            return Math.min(scaleX, scaleY);
        }, mainContainer.widthProperty(), mainContainer.heightProperty());

        masterGroup.scaleXProperty().bind(scaleBinding);
        masterGroup.scaleYProperty().bind(scaleBinding);
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
