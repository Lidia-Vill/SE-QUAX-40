package com.example.sequax40.controller;


import com.example.sequax40.enums.ShapeEnum;
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
    @FXML public StackPane mainContainer;
    @FXML public Group masterGroup;

    //setting up ids of the board stack pane and group to format shape of game
    @FXML public StackPane gameBoardStackPane;
    @FXML public Group boardGroup;

    //setting original board width to calculate scaling
    private final double DESIGN_WIDTH = 845.0;
    private final double DESIGN_HEIGHT = 845.0;


    public Board board;

    public Map<String, Tile> tileMap = new HashMap<>();
    public Map<String, Polygon> polygonMap = new HashMap<>();


    //initialise method 
    @FXML
    public void initialize() {

        masterGroup.setManaged(true); //consider the groups bounds when calculating the layout

        StackPane.setAlignment(masterGroup, javafx.geometry.Pos.CENTER); //position the main stackpane to center on the window

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


        board = new Board(11, 11);
        setupTiles();
    }

    public void setupTiles() {
        for (var node : boardGroup.getChildren()) {
            if (!(node instanceof Polygon polygon)) continue;

            String fxId = polygon.getId();
            if (fxId == null || fxId.isBlank()) continue;

            // dtermine tile type by fxId pattern or length
            ShapeEnum shapeType = (fxId.length() <= 3) ? ShapeEnum.OCTAGON : ShapeEnum.RHOMBUS;

            // create or get Tile from the board
            Tile tile = board.getTile(fxId); // assumes Board can return a Tile for any ID
            if (tile == null) {
                tile = new Tile(fxId, shapeType); // for rhombuses or missing ones
                board.addTile(tile);
            }

            // store Tile in polygon's userData for easy access in clicks
            polygon.setUserData(tile);

            // set initial color
            polygon.setFill(DEFAULT_COLOR);

            // set click handler
            polygon.setOnMouseClicked(this::handleTileClick);

            // save in our maps
            tileMap.put(fxId, tile);
            polygonMap.put(fxId, polygon);
        }
    }

    @FXML
    public void handleTileClick(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof Polygon clicked)) return;

        // get tile model associated 
        Tile tile = null;
        Object userData = clicked.getUserData();
        if (userData instanceof Tile t) {
            tile = t;
        }

        // find the default colour based on tile type
        Color defaultColor;
        if (tile != null) {
            defaultColor = (tile.getShape() == ShapeEnum.OCTAGON) ? Color.web("#4d44ff") : Color.web("#9e9bec");
        } else {
            defaultColor = Color.LIGHTGRAY; // if no tile 
        }

        // toggle selection and set fill
        if (tile != null) {
            tile.toggleSelected();
            clicked.setFill(tile.isSelected() ? SELECTED_COLOR : defaultColor);
        } else {
            Object stored = clicked.getProperties().getOrDefault("originalColor", clicked.getFill());
            Color originalColor = (stored instanceof Color c) ? c : defaultColor;

            clicked.getProperties().putIfAbsent("originalColor", originalColor);
            clicked.setFill(clicked.getFill().equals(SELECTED_COLOR) ? originalColor : SELECTED_COLOR);
        }
    }





}
