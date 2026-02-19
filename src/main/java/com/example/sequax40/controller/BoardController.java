package com.example.sequax40.controller;


import com.example.sequax40.model.board.Tile;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import com.example.sequax40.model.board.Board;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;

import java.util.HashMap;
import java.util.Map;

public class BoardController {


    //to test the tiles are clickable
    private static final Color SELECTED_COLOR = Color.WHITE;
    private static final Color DEFAULT_COLOR = Color.web("#4d44ff");

    //setting up the ids of the stackpane container, and group to help with scaling
    @FXML
    public StackPane mainContainer;
    @FXML
    public Group masterGroup;

    //setting up ids of the board stack pane and group to format shape of game
    @FXML
    private StackPane gameBoardStackPane;
    @FXML
    private Group boardGroup;

    //setting original board width to calculate scaling
    private final double DESIGN_WIDTH = 900.0;
    private final double DESIGN_HEIGHT = 850.0;


    // --- Board model ---
    public Board board;

    // Map from fx:id to Polygon for easy access
    public final Map<String, Polygon> tileMap = new HashMap<>();

    //initialise method 
    @FXML
    public void initialize() {

        masterGroup.setManaged(true); //consider the groups bounds when calculating the layout
        //masterGroup.setTranslateY(-100); //hardcoded as the board displays in the center of the window, this removes the padding

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


        // BOARD LOGIC
        board = new Board(11, 11);
        setupOctagonTiles();
    }

    private void setupOctagonTiles() {
        // Iterate over all children in boardGroup (Scene Builder polygons)
        for (var node : boardGroup.getChildren()) {
            if (node instanceof Polygon polygon) {
                String fxId = polygon.getId(); // fx:id from Scene Builder, e.g., "A1", "B2"
                if (fxId != null && !fxId.isBlank()) {
                    Tile tile = board.getTile(fxId); // get corresponding Tile
                    polygon.setUserData(tile);       // store model in polygon
                    polygon.setFill(DEFAULT_COLOR);  // initial color

                    polygon.setOnMouseClicked(this::handleCellClick);

                    tileMap.put(fxId, polygon);      // save for future use
                }
            }
        }
    }


    //method to make the cells in the board clickable to then implement rules and enforce player turns later
    @FXML
    public void handleCellClick(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof Polygon clicked)) return;

        // Get the model Tile associated with this polygon
        Object userData = clicked.getUserData();
        com.example.sequax40.model.board.Tile tile = null;

        if (userData instanceof com.example.sequax40.model.board.Tile t) {
            tile = t;
        }

        // Get original color stored in polygon
        Color originalColor = (Color) clicked.getProperties().getOrDefault("originalColor", clicked.getFill());

        // Store original color if not already stored
        clicked.getProperties().putIfAbsent("originalColor", originalColor);

        // Toggle selection
        if (tile != null) {
            tile.toggleSelected();
            clicked.setFill(tile.isSelected() ? SELECTED_COLOR : originalColor);
        } else {
            // fallback: just toggle color if no tile
            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor);
            } else {
                clicked.setFill(SELECTED_COLOR);
            }
        }
    }

    // Add this inside BoardController
    public void handleTileClick(MouseEvent event) {
        Polygon clicked = (Polygon) event.getSource();
        Tile tile = (Tile) clicked.getUserData();

        // Toggle the model
        tile.toggleSelected();

        // Update the UI color
        clicked.setFill(tile.isSelected() ? SELECTED_COLOR : DEFAULT_COLOR);
    }


}
