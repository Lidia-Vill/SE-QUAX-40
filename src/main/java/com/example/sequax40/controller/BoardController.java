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

       
    //to test the tiles are clickable 
    private static final Color SELECTED_COLOR = Color.WHITE;

    //setting up the ids of the stackpane container, and group to help with scaling
    @FXML public StackPane mainContainer; 
    @FXML public Group masterGroup; 
    
    //setting up ids of the board stack pane and group to format shape of game
    @FXML private StackPane gameBoardStackPane;
    @FXML private Group boardGroup;

    //setting original board width to calculate scaling
    private final double DESIGN_WIDTH = 900.0; 
    private final double DESIGN_HEIGHT = 850.0; 

    //initialise method 
    @FXML
    public void initialize() {

        masterGroup.setManaged(true); //consider the groups bounds when calculating the layout
        masterGroup.setTranslateY(-100); //hardcoded as the board displays in the center of the window, this removes the padding 

        StackPane.setAlignment(masterGroup, javafx.geometry.Pos.TOP_CENTER); //position the main stackpain

        NumberBinding scaleBinding = Bindings.createDoubleBinding(() -> {
            double containerWidth = mainContainer.getWidth(); //find width of window
            double containerHeight = mainContainer.getHeight(); //find height of window
            
            if (containerWidth <= 0 || containerHeight <= 0) {
            	return 1.0;
            }

            double scaleX = containerWidth / DESIGN_WIDTH; //calculate scale of the x of board
            double scaleY = containerHeight / DESIGN_HEIGHT; //calculate scale of y of board 

            return Math.min(scaleX, scaleY); //find the min of x or y to ensure the board always fits the viewport 
        }, mainContainer.widthProperty(), mainContainer.heightProperty());

        //ensures the board keeps the aspect ratio without stretching
        masterGroup.scaleXProperty().bind(scaleBinding);
        masterGroup.scaleYProperty().bind(scaleBinding);
    }

    //method to make the cells in the board clickable to then implement rules and enforce player turns later
    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();


        Color originalColor = (Color) clicked.getUserData(); //check if tile is already clicked

        if (originalColor == null) {
            clicked.setUserData(clicked.getFill());
            clicked.setFill(SELECTED_COLOR);
        } 
        else {
            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor); //if already clicked, return to original colour
            } else {
                clicked.setFill(SELECTED_COLOR); //otherwise make it right colour
            }
        }
    }
}
