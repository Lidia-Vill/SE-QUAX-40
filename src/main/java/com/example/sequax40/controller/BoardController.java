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
    @FXML public HBox windowContainer;
    //turn label
    @FXML public Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;
	
   
    //setting original board width to calculate scaling
    private final static double DESIGN_WIDTH = 1100.0;
    private final static double DESIGN_HEIGHT = 845.0;

    //Model
    public Board board;
    private GameManager gameManager;

    public Map<String, Tile> tileMap = new HashMap<>();
    public Map<String, Polygon> polygonMap = new HashMap<>();
    


    //initialise method 
    @FXML
    public void initialize() {

        setupScaling(); //call the scaling method 
                
        if (this.board == null) {
            this.board = new Board(11, 11);
        }
        setupTiles();
        
        gameManager = new GameManager(board, tileMap); //initialise the game manager to enforce rules
           
        updateTurnLabel(); //after resetting the game, this updates the UI text which then displays "Black's turn"
    }

    
    private void setupScaling() { //helper method to scale the board to fit screen
    	
    	NumberBinding scaleBinding = Bindings.createDoubleBinding(() -> {
            
    		double containerWidth = mainContainer.getWidth(); //find width of window
            double containerHeight = mainContainer.getHeight(); //find height of window

            if (containerWidth <= 0 || containerHeight <= 0) {
                return 1.0;
            }

            double scaleX = containerWidth / getDESIGN_WIDTH(); //calculate scale of the x of board
            double scaleY = containerHeight / DESIGN_HEIGHT; //calculate scale of y of board 

            return Math.min(scaleX, scaleY); //find the min of x or y to ensure the board always fits the viewport
            
        }, mainContainer.widthProperty(), mainContainer.heightProperty());

        //ensures the board keeps the aspect ratio without stretching
        windowContainer.scaleXProperty().bind(scaleBinding);
        windowContainer.scaleYProperty().bind(scaleBinding);
        windowContainer.setManaged(true);
		
        StackPane.setAlignment(masterGroup,  javafx.geometry.Pos.CENTER);

    }
    
    //initialises all tiles in ui by scanning board group 
    public void setupTiles() {
    	if (boardGroup == null) {
            return; 
        }
        
        attachTiles(boardGroup);
    }
    

    private void attachTiles(javafx.scene.Parent parent) {
        //loop through all child nodes of current parent
    	for (var node : parent.getChildrenUnmodifiable()) {

    		//check if node is a polygon (represents tile)
            if (node instanceof Polygon polygon) {

            	//get the fx id assigned in fxml
                String fxId = polygon.getId();
                //and skips shapes without an id
                if (fxId == null || fxId.isBlank()) continue;

                //determines type of shape based on length of id
                ShapeEnum shapeType = (fxId.length() <= 3)
                        ? ShapeEnum.OCTAGON
                        : ShapeEnum.RHOMBUS;

                // look up corresponding tile in board model
                Tile tile = board.getTile(fxId);
                //if tile doesn't exist in board model skip 
                if (tile == null) {
                    continue;
                }

                //attach the tile to the polygon to retrieve it later
                polygon.setUserData(tile);
                //set the colour based on the shape
                polygon.setFill(shapeType == ShapeEnum.OCTAGON ? Color.web("#4d44ff") : Color.web("#9e9bec"));
                //add a click handler so tile reacts to user interaction 
                polygon.setOnMouseClicked(this::handleTileClick);

                //store the references for lookup
                tileMap.put(fxId, tile);
                polygonMap.put(fxId, polygon);

            } //if node is in another container recursively search for polygons 
            else if (node instanceof javafx.scene.Parent childParent) {
                attachTiles(childParent); // recurse into nested groups
            }
        }
    }

    
    @FXML
    public void handleTileClick(MouseEvent event) {
            	
        if (!(event.getSource() instanceof Polygon clicked)) {
        	return;
        }
        
        Tile tile = (clicked.getUserData() instanceof Tile t) ? t : null;
            
        //if tile is not initialised or if its already owned, return (allows player to click again as invalid move)
        if (tile == null || !tile.isEmpty()) {
        	return;
        }

        //check if the move made is valid with the rules in game manager
        boolean movePlayed = gameManager.makeMove(tile);
        if(!movePlayed) {
        	return; //if invalid move, return
        }
        
        updateTileUI(tile, clicked); //update the tile the player clicked to match their colour
        updateTurnLabel(); //update the display to show the next player
    }
    
    
    private void updateTileUI(Tile tile, Polygon polygon) {
    	//update the colour of the tile clicked based on its owner
    	if (tile.getOwner() == PlayerEnum.BLACK) {
            polygon.setFill(Color.web("#2f2f2f"));
        } 
    	else {
            polygon.setFill(Color.WHITE);
        }
    }


    private void updateTurnLabel() {
    	
    	//find out whos turn it is after the last move
    	PlayerEnum currentTurn = gameManager.getCurrentTurn();
    	
    	//change the turn displays colour and text based on currentTurn
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
        resetGame();//when player clicks the reset button, this method is called
    }

    
    public void resetGame() {

        // Reset the board model (the game logic)
        gameManager.resetGame();

        // Reset the UI colours
        for (Map.Entry<String, Polygon> entry : polygonMap.entrySet()) { //loops through every tile on the board

            Polygon poly = entry.getValue();

            Object data = poly.getUserData();
            Tile tile = null;

            if (data instanceof Tile) {
                tile = (Tile) data;
            }

            if (tile == null) { //if tile is missing then we skip
                continue;
            }

            poly.setFill(getDefaultFill(tile)); //changes tile back to its original colour
        }

        updateTurnLabel(); //reset to black
    }
    
    
    //original colour of tiles
    private Color getDefaultFill(Tile tile) {
        if (tile.getShape() == ShapeEnum.OCTAGON) {
            return Color.web("#4d44ff");
        }
        else {
            return Color.web("#9e9bec");
        }
    }


    // setters used for testing so UI fields are not null (Sprint2 Features 1 and 3 test)
    public void setTurnLabel(Label label) {
        this.turnLabel = label;
    }

    public void setTurnOct(Polygon oct) {
        this.turnOct = oct;
    }

    public void setTurnRhom(Polygon rhom) {
        this.turnRhom = rhom;
    }
    
    public void setBoard(Board board) {
    	this.board = board;
    }
    
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    public void setMainContainer(StackPane mainContainer) {
        this.mainContainer = mainContainer;
    }
    
    public void setWindowContainer(HBox windowContainer) {
        this.windowContainer = windowContainer;
    }
    
    public void setMasterGroup(Group masterGroup) {
    	this.masterGroup = masterGroup;
    }
    
    public void setBoardGroup(Group boardGroup) {
        this.boardGroup = boardGroup;
    }
    
    //getters for testing
    public Label getTurnLabel() {
    	return turnLabel;
    }


	public static double getDESIGN_WIDTH() {
		return DESIGN_WIDTH;
	}
	
	public static double getDESIGN_HEIGHT() {
		return DESIGN_HEIGHT;
	}
}

