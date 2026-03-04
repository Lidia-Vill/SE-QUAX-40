package com.example.sequax40.controller;


import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.model.board.Board;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;

import java.util.HashMap;
import java.util.Map;


public class BoardController {

	// FXML
	//setting up the ids of the stackpane container, and group to help with scaling
    @FXML public StackPane mainContainer;
    @FXML public Group masterGroup;
    //setting up ids of the board stack pane and group to format shape of game
    @FXML public StackPane gameBoardStackPane;
    @FXML public Group boardGroup;
    @FXML private HBox windowContainer;
    //turn label
    @FXML private Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;
	
   
    //setting original board width to calculate scaling
    private final double DESIGN_WIDTH = 1100.0;
    private final double DESIGN_HEIGHT = 845.0;

    //Model
    public Board board;
    private GameManager gameManager;

    public Map<String, Tile> tileMap = new HashMap<>();
    public Map<String, Polygon> polygonMap = new HashMap<>();
    


    //initialise method 
    @FXML
    public void initialize() {

        setupScaling();
                
        board = new Board(11, 11);
        setupTiles();
        
        gameManager = new GameManager(board, tileMap);
           
        updateTurnLabel();
    }

    
    private void setupScaling() {
    	
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
        windowContainer.scaleXProperty().bind(scaleBinding);
        windowContainer.scaleYProperty().bind(scaleBinding);
        windowContainer.setManaged(true);
		
        StackPane.setAlignment(masterGroup,  javafx.geometry.Pos.CENTER);

    }
    
    
    public void setupTiles() {
        attachTilesRecursively(boardGroup);
    }
    

    private void attachTilesRecursively(javafx.scene.Parent parent) {
        for (var node : parent.getChildrenUnmodifiable()) {

            if (node instanceof Polygon polygon) {

                String fxId = polygon.getId();
                if (fxId == null || fxId.isBlank()) continue;

                ShapeEnum shapeType = (fxId.length() <= 3)
                        ? ShapeEnum.OCTAGON
                        : ShapeEnum.RHOMBUS;

                // Always create a Tile if not in board
                Tile tile = board.getTile(fxId);
                if (tile == null) {
                    continue;
                }

                polygon.setUserData(tile);
                polygon.setFill(shapeType == ShapeEnum.OCTAGON ? Color.web("#4d44ff") : Color.web("#9e9bec"));
                polygon.setOnMouseClicked(this::handleTileClick);

                tileMap.put(fxId, tile);
                polygonMap.put(fxId, polygon);

            } else if (node instanceof javafx.scene.Parent childParent) {
                attachTilesRecursively(childParent); // recurse into nested groups
            }
        }
    }

    
    @FXML
    public void handleTileClick(MouseEvent event) {
            	
        if (!(event.getSource() instanceof Polygon clicked)) {
        	return;
        }
        
        Tile tile = (clicked.getUserData() instanceof Tile t) ? t : null;
              
        if (tile == null || !tile.isEmpty()) {
        	return;
        }


        boolean movePlayed = gameManager.playMove(tile);
        if(!movePlayed) {
        	return;
        }
        
        updateTileUI(tile, clicked);
        updateTurnLabel();
    }
    
    
    private void updateTileUI(Tile tile, Polygon polygon) {
    	if (tile.getOwner() == PlayerEnum.BLACK) {
            polygon.setFill(Color.web("#2f2f2f"));
        } 
    	else {
            polygon.setFill(Color.WHITE);
        }
    }


    private void updateTurnLabel() {
    	
    	PlayerEnum currentTurn = gameManager.getCurrentTurn();
    	
    	if(currentTurn == PlayerEnum.BLACK) {
    		turnLabel.setText("BLACKS'S TURN");
    		turnLabel.setTextFill(Color.web("2f2f2f"));
    		//change colour of octagon & rhombus 
    		turnOct.setFill(Color.web("#2f2f2f"));
            turnRhom.setFill(Color.web("#2f2f2f"));

    	}
    	else {
    		turnLabel.setText("WHITE'S TURN");
    		turnLabel.setTextFill(Color.WHITE);
    		turnOct.setFill(Color.WHITE);
            turnRhom.setFill(Color.WHITE);
    	}
    }


    @FXML
    private void handleReset() {
        resetGame();
    }

    
    public void resetGame() {

        // Reset the board model
        gameManager.resetGame();

        // Reset the UI colours
        for (Map.Entry<String, Polygon> entry : polygonMap.entrySet()) {

            Polygon poly = entry.getValue();

            Object data = poly.getUserData();
            Tile tile = null;

            if (data instanceof Tile) {
                tile = (Tile) data;
            }

            if (tile == null) {
                continue;
            }

            poly.setFill(getDefaultFill(tile));
        }

        updateTurnLabel();
    }
    
    
    //Put back to original colour
    private Color getDefaultFill(Tile tile) {
        if (tile.getShape() == ShapeEnum.OCTAGON) {
            return Color.web("#4d44ff");
        }
        else {
            return Color.web("#9e9bec");
        }
    }


    // setters used for testing so UI fields are not null (Sprint2 Feature3 test)
    public void setTurnLabel(Label label) {
        this.turnLabel = label;
    }

    public void setTurnOct(Polygon oct) {
        this.turnOct = oct;
    }

    public void setTurnRhom(Polygon rhom) {
        this.turnRhom = rhom;
    }
}

