package com.example.sequax40.controller;


import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.model.board.Board;

import com.example.sequax40.model.player.BotPlayer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


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
    //pie rule button
    @FXML private Button pieRuleButton;
	
   
    //setting original board width to calculate scaling
    private final static double DESIGN_WIDTH = 1100.0;
    private final static double DESIGN_HEIGHT = 845.0;

    //Model
    public Board board;
    private GameManager gameManager;

    public Map<String, Tile> tileMap = new HashMap<>();
    public Map<String, Polygon> polygonMap = new HashMap<>();

    private boolean pieRuleUsed = false;

    private BotPlayer botPlayer = new BotPlayer();
    private final Random random = new Random();

    // BOT is BLACK for now (change later if needed)
    private PlayerEnum botColor = PlayerEnum.BLACK;

    private boolean botThinking = false;


    //initialise method 
    @FXML
    public void initialize() {

        setupScaling();

        if (this.board == null) {
            this.board = new Board(11, 11);
        }

        setupTiles();
        pieRuleButton.setVisible(false);

        gameManager = new GameManager(board, tileMap);

        updateTurnLabel();

        // safe start
        Platform.runLater(this::triggerBotIfNeeded);
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

        if (botThinking) return;

        if (!(event.getSource() instanceof Polygon clicked)) {
            return;
        }

        Tile tile = (clicked.getUserData() instanceof Tile t) ? t : null;

        // if tile is not initialised or if it's already owned, return
        if (tile == null || !tile.isEmpty()) {
            return;
        }

        // check if the move made is valid with the rules in game manager
        boolean movePlayed = gameManager.makeMove(tile);
        if (!movePlayed) {
            return; // if invalid move, return
        }

        // colour the tile immediately, including the winning tile
        updateTileUI(tile, clicked);

        if (gameManager.isGameOver()) {
            turnLabel.setText(gameManager.getCurrentTurn() + " WINS!");
            return;
        }

        updateTurnLabel();
        updatePieRuleButtonVisibility();

        // SAFE bot trigger
        Platform.runLater(this::triggerBotIfNeeded);
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
    
    //a method to handle player clicking the pie rule button
    @FXML
    public void handlePieRule(ActionEvent event) {
        	
    	if(isPieRuleUsed() || gameManager.getMoveCount() != 1) {
    		return;
    	} //if the rule has already been used and there's more or less than 1 move made don't do anything if its clicked
    	
    	setPieRuleUsed(true); //mark the use as used 
    	
    	Tile firstTile = gameManager.getFirstMoveTile(); //find Blacks first tile 
    	
    	if(firstTile != null) {
    		firstTile.setOwner(PlayerEnum.WHITE); //change the owner to white
    		
    		Polygon polygon = polygonMap.get(firstTile.getCoord());
    		
    		if(polygon != null) {
    			updateTileUI(firstTile, polygon); //change the colour of the tile to white to match new owner
    		}
    	}

        gameManager.switchTurn(); //switch the turn back to black
 
        updateTurnLabel(); //update the turn label to say blacks turn
        
        Platform.runLater(this::triggerBotIfNeeded); //trigger the bot to continue gameplay
    	
    }
    
    //a method to change the visibility of the pie rule button
    public void updatePieRuleButtonVisibility() {
    	if(pieRuleButton == null) {
    		return;
    	}
    	    	
    	pieRuleButton.setVisible(shouldShowPieRuleButton());
    }
    
    //boolean method to check whether button should be shown
    public boolean shouldShowPieRuleButton() {
    	return gameManager.getMoveCount() == 1 && !pieRuleUsed;
    }


    private void updateTurnLabel() {
    	
    	//find out whos turn it is after the last move
    	PlayerEnum currentTurn = gameManager.getCurrentTurn();
 
    	
    	//change the turn displays colour and text based on currentTurn
    	if(currentTurn == PlayerEnum.BLACK) {
    		turnLabel.setText("BLACK'S TURN");
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

        gameManager.resetGame();

        pieRuleUsed = false;

        updatePieRuleButtonVisibility();

        for (Map.Entry<String, Polygon> entry : polygonMap.entrySet()) {

            Polygon poly = entry.getValue();
            Tile tile = (Tile) poly.getUserData();

            if (tile == null) continue;

            poly.setFill(getDefaultFill(tile));
        }

        updateTurnLabel();

        Platform.runLater(this::triggerBotIfNeeded);
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
    
    public void setPieRuleButton(Button pieRuleButton) {
    	this.pieRuleButton = pieRuleButton;
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


	public boolean isPieRuleUsed() {
		return pieRuleUsed;
	}


	public void setPieRuleUsed(boolean pieRuleUsed) {
		this.pieRuleUsed = pieRuleUsed;
		updatePieRuleButtonVisibility();
	}


	public Node getPieRuleButton() {
		// TODO Auto-generated method stub
		return pieRuleButton;
	}


    // Checks if it is currently the bot's turn
    private boolean isBotTurn() {
        return gameManager.getCurrentTurn() == botColor;
    }


    // Triggers the bot to make a move IF needed
    private void triggerBotIfNeeded() {
    	
    	// wait for player to decide pie rule
    	if (shouldShowPieRuleButton()) return;

        // If the game is already finished do nothing
    	if (gameManager.isGameOver()) return;

        // If it's not the bot's turn do nothing
        if (gameManager.getCurrentTurn() != botColor) return;

        // Mark that the bot is currently thinking (used to disable clicks)
        botThinking = true;

        // Create a short delay (0.4 seconds) to make bot feel more natural
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.4));

        // After the delay finishes bot makes its move
        pause.setOnFinished(e -> makeBotMove());

        // Start the delay
        pause.play();
    }

    private void makeBotMove() {

        List<Tile> availableTiles = tileMap.values().stream()
                .filter(Tile::isEmpty)
                .toList();

        if (availableTiles.isEmpty()) return;

        Tile chosenTile;

        // First move always to F6 as it is the smartest play (for now)
        if (gameManager.getMoveCount() == 0) {

            chosenTile = availableTiles.stream()
                    .filter(tile -> "F6".equals(tile.getCoord()))
                    .findFirst()
                    .orElse(null);

        } else {
            // Otherwise pick a random tile
            chosenTile = availableTiles.get(random.nextInt(availableTiles.size()));
        }

        if (chosenTile == null) return;

        boolean movePlayed = gameManager.makeMove(chosenTile);

        int attempts = 0;


        // If the move was invalid, retry with different random tiles
        // (prevents bot getting stuck on invalid placements)
        while (!movePlayed && attempts < 100) {

            chosenTile = availableTiles.get(random.nextInt(availableTiles.size()));

            movePlayed = gameManager.makeMove(chosenTile);

            attempts++;
        }

        // Update the UI for the chosen tile (change colour)
        Polygon polygon = polygonMap.get(chosenTile.getCoord());
        if (polygon != null) {
            updateTileUI(chosenTile, polygon);
        }
        
                // Check if the move ended the game
        if (gameManager.isGameOver()) {
            turnLabel.setText(gameManager.getCurrentTurn() + " WINS!");
            return;
        }

        updateTurnLabel();
        botThinking = false;

        // If next turn is also bot (future-proofing), trigger again
        Platform.runLater(this::triggerBotIfNeeded);
        
        updatePieRuleButtonVisibility();
    }
}

