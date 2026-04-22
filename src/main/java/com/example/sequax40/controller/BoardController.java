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

import java.util.*;


public class BoardController {


    @FXML public StackPane mainContainer;
    @FXML public Group masterGroup;

    @FXML public StackPane gameBoardStackPane;
    @FXML public Group boardGroup;
    @FXML public HBox windowContainer;

    @FXML public Label turnLabel;
    @FXML private Polygon turnOct;
    @FXML private Polygon turnRhom;

    @FXML private Button pieRuleButton;

    @FXML private Button showStratButton;
    @FXML private Label strategyLabel1;
    @FXML private Label strategyLabel2;

    @FXML private Label timerLabel;
    private javafx.animation.Timeline gameTimer;
    private int elapsedSeconds = 0;


    // Bot Strategy related variables
    private List<Tile> currentStrategyPath = new ArrayList<>();
    private boolean strategyVisible = false;

    // Highlight colours for strategy display
    private static final Color STRATEGY_PATH_COLOR  = Color.web("#ff9800"); // orange
    private static final Color STRATEGY_NEXT_COLOR  = Color.web("#ffeb3b"); // yellow
    
    
   
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

        startTimer(); //start the timer
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
            return;
        }
        
        if (strategyVisible) {
            hideStrategy();
        }

        // colour the tile immediately, including the winning tile
        updateTileUI(tile, clicked);

        if (gameManager.isGameOver()) {
            turnLabel.setText(gameManager.getCurrentTurn() + " WINS!");
            stopTimer();
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
        
        if (strategyVisible) {
            hideStrategy();
        }
 
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
        hideStrategy();
        gameManager.resetGame();

        
        pieRuleUsed = false;
        updatePieRuleButtonVisibility();

        for (Map.Entry<String, Polygon> entry : polygonMap.entrySet()) {

            Polygon poly = entry.getValue();
            Tile tile = (Tile) poly.getUserData();

            if (tile == null) continue;

            poly.setFill(getDefaultFill(tile));
        }

        startTimer(); //restart the timer
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

        if (shouldShowPieRuleButton()) return;
        if (gameManager.isGameOver()) return;
        if (gameManager.getCurrentTurn() != botColor) return;

        botThinking = true;

        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.4));

        pause.setOnFinished(e -> {
            if (gameManager.getCurrentTurn() != botColor) {
                botThinking = false;
                return;
            }
            makeBotMove();
        });

        pause.play();
    }

    private void makeBotMove() {
    	 

        if (gameManager.getCurrentTurn() != botColor) {
            botThinking = false;
            return;
        }
 

        BotPlayer.StrategyResult strategy =
                botPlayer.computeStrategy(tileMap, botColor, gameManager.getMoveCount());
 
        Tile chosenTile = (strategy != null) ? strategy.chosenTile : null;

        if (chosenTile == null) {
            chosenTile = tileMap.values().stream()
                    .filter(t -> t.isEmpty()
                            && t.getShape() == com.example.sequax40.enums.ShapeEnum.OCTAGON)
                    .findFirst()
                    .orElse(null);
        }

        
        if (chosenTile == null) {
            botThinking = false;
            return;
        }
 

        if (strategy != null) {
            botPlayer.setLastExecutedStrategy(strategy);
        }
 

        boolean movePlayed = gameManager.makeMove(chosenTile);
        if (!movePlayed) {

            botThinking = false;
            Platform.runLater(this::triggerBotIfNeeded);
            return;
        }
 
        Polygon polygon = polygonMap.get(chosenTile.getCoord());
        if (polygon != null) {
            updateTileUI(chosenTile, polygon);
        }

        if (gameManager.isGameOver()) {
            turnLabel.setText(gameManager.getCurrentTurn() + " WINS!");
            stopTimer();
            botThinking = false;
            return;
        }
 
        updateTurnLabel();
        updatePieRuleButtonVisibility();
        botPlayer.clearCache();
        botThinking = false;
 
 
        Platform.runLater(this::triggerBotIfNeeded);
    }


    public void setStrategyLabel1(Label label) {
        this.strategyLabel1 = label;
    }

    public void setStrategyLabel2(Label label) {
        this.strategyLabel2 = label;
    }

    public void setShowStratButton(Button button) {
        this.showStratButton = button;
    }

    public List<Tile> getCurrentStrategyPath() {
        return currentStrategyPath;
    }

    public void setBotPlayer(BotPlayer botPlayer) {
        this.botPlayer = botPlayer;
    }



    @FXML
    public void showStrat(ActionEvent event) {
 
        if (strategyVisible) {
            hideStrategy();
            return;
        }
 
        // Prefer cached (current turn) over last executed
        BotPlayer.StrategyResult strategy = botPlayer.getCachedStrategy();
        if (strategy == null) strategy = botPlayer.getLastExecutedStrategy();
        if (strategy == null) return;
 
        currentStrategyPath = strategy.path;
        Tile chosenTile     = strategy.chosenTile;
 
        for (Tile tile : currentStrategyPath) {
 
            Polygon poly = polygonMap.get(tile.getCoord());
            if (poly == null) continue;
 
            boolean isChosen = chosenTile != null
                    && tile.getCoord().equals(chosenTile.getCoord());
 
            // Chosen tile always shown in yellow, regardless of shape
            if (isChosen) {
                poly.setFill(STRATEGY_NEXT_COLOR);
                continue;
            }
 
            // Rhombus tiles in path: highlight in orange (even if already owned)
            if (tile.getShape() == ShapeEnum.RHOMBUS) {
                if(tile.isEmpty()) {
                	poly.setFill(STRATEGY_PATH_COLOR);
                }
                continue;
            }
 
            // Octagon: highlight only empty tiles (owned tiles keep their player colour)
            if (tile.isEmpty()) {
                poly.setFill(STRATEGY_PATH_COLOR);
            }
        }
        
        String explanation = "";
        
        if(pieRuleButton.isVisible()) {
        	explanation="Bot chooses centre tile on its first move as it is generally seen as an optimal starting position.";
        }
        else {
        	explanation = "The bot uses Dijkstra's Algorithm to calculate the best path, "
            		+ "treating all tiles as connected nodes with different costs: connected tiles (0), "
            		+ "empty rhombuses (1)(so they’re preferred), empty octagons (2), and opponent tiles are blocked. "
            		+ "It follows the lowest-cost path, but if the opponent is close to winning, it switches to a blocking "
            		+ "strategy by placing a tile on the opponent’s best path to slow them down. "
            		+ "Otherwise, it focuses on completing its own path as efficiently as possible. ";
        }
        
        if (strategyLabel1 != null) strategyLabel1.setVisible(true);
        if (strategyLabel2 != null) {
            strategyLabel2.setText(explanation);
            strategyLabel2.setVisible(true);
            strategyLabel2.setManaged(true);
        }
 
        showStratButton.setText("HIDE STRATEGY");
        strategyVisible = true;
    }


    private void hideStrategy() {
        for (Tile tile : currentStrategyPath) {

            Polygon poly = polygonMap.get(tile.getCoord());
            if (poly == null) continue;

            // Restore correct colour based on current owner
            if (!tile.isEmpty()) {
                updateTileUI(tile, poly);
            } else {
                poly.setFill(getDefaultFill(tile));
            }
        }
        
        if (strategyLabel1 != null) strategyLabel1.setVisible(false);
        if (strategyLabel2 != null) {
            strategyLabel2.setVisible(false);
            strategyLabel2.setText("");
            strategyLabel2.setManaged(true);
        }

        showStratButton.setText("SHOW STRATEGY");

        currentStrategyPath = new ArrayList<>();
        strategyVisible = false;
    }


    private void startTimer() {
        elapsedSeconds = 0;
        updateTimerLabel();
        if (gameTimer != null) gameTimer.stop();

        gameTimer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1),
                        e -> {
                            elapsedSeconds++;
                            updateTimerLabel();
                        }
                )
        );
        gameTimer.setCycleCount(javafx.animation.Animation.INDEFINITE);
        gameTimer.play();
    }

    private void stopTimer() {
        if (gameTimer != null) gameTimer.stop();
    }

    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        if (timerLabel != null) {
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

	public void setTimerLabel(Label label) {
		this.timerLabel = label;		
	}
	
	public void updateTimerLabelForTest() {
		updateTimerLabel();
	}
	
	
	public void startTimerForTest() { 
		startTimer(); 
	}
	
	public void stopTimerForTest()  { 
		stopTimer(); 
	}
	
	
	public int getElapsedSeconds() { 
		return elapsedSeconds; 
	}
	
	public void setElapsedSeconds(int seconds) {
		this.elapsedSeconds = seconds;
	}
	
	
	public javafx.animation.Timeline getGameTimer() { 
		return gameTimer; 
	}
	
	public void setGameTimer(javafx.animation.Timeline timer) {
		this.gameTimer = timer;
	}

}
