package com.example.sequax40.controller;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.player.BotPlayer;
import com.example.sequax40.model.move.Move;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.beans.binding.*;
import javafx.util.Duration;

import java.util.*;

/*
 * Controller for the game board view
 * Manages user interaction, bot moves, tile rendering and game state display 
 */

public class BoardController {
	
	// Design Constants
	
	private final static double DESIGN_WIDTH = 1100.0;
    private final static double DESIGN_HEIGHT = 845.0;
    
    // Tile Colours
    
    private static final Color COLOUR_OCTAGON_DEFAULT = Color.web("#4d44ff");
    private static final Color COLOUR_RHOMBUS_DEFAULT = Color.web("#9e9bec");
    private static final Color COLOUR_PLAYER_BLACK    = Color.web("#2f2f2f");
    private static final Color COLOUR_PLAYER_WHITE    = Color.WHITE;

    // Strategy Path Colours 
    
    private static final Color STRATEGY_PATH_COLOUR  = Color.web("#ff9800"); // orange
    private static final Color STRATEGY_LAST_TILE_COLOUR  = Color.web("#ffeb3b"); // yellow
    private static final Color STRATEGY_BLOCK_COLOUR = Color.web("#e53935"); // red
    
    // BOT Configuration 
    
    private static final PlayerEnum BOT_COLOUR = PlayerEnum.BLACK;
    private static final double BOT_MOVE_DELAY_SECONDS = 0.4;
    
    // FXML Injected Fields

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
    @FXML private Label strategyLabelTitle;
    @FXML private Label strategyLabelText;
    @FXML private ScrollPane strategyScrollPane;
    @FXML private Label timerLabel;

    // Model
    
    public Board board;
    private GameManager gameManager;
    private BotPlayer botPlayer = new BotPlayer();

    // Tile Lookup Maps
    
    public Map<String, Tile> tileMap = new HashMap<>();
    public Map<String, Polygon> polygonMap = new HashMap<>();
    
    // Game State
    
    private boolean pieRuleUsed = false;
    private boolean botThinking = false;
    private boolean strategyVisible = false;
    
    private List<Tile> currentStrategyPath = new ArrayList<>();

    // Timer State

    private Timeline gameTimer;
    private int elapsedSeconds = 0;


    // - Initialisation ---------------------------------------------------------------------------------------------

    @FXML
    public void initialize() {
        setupScaling();
        initBoard();
        setupTiles();
        pieRuleButton.setVisible(false);
        gameManager = new GameManager(board, tileMap);
        startTimer(); 
        updateTurnLabel();
        Platform.runLater(this::triggerBotIfNeeded);
    }
    
    private void initBoard() {
    	if(this.board == null) {
    		this.board = new Board(11, 11);
    	}
    }

    /*
     * Binds the window container's scale to fit within main container
     * 	while preserving design aspect ration 
     */
    private void setupScaling() { 
    	
    	NumberBinding scaleBinding = Bindings.createDoubleBinding(
    		this::computeScale, 
    		mainContainer.widthProperty(), 
    		mainContainer.heightProperty()
        );
        windowContainer.scaleXProperty().bind(scaleBinding);
        windowContainer.scaleYProperty().bind(scaleBinding);
        windowContainer.setManaged(true);
        StackPane.setAlignment(masterGroup,  javafx.geometry.Pos.CENTER);
    }
    
    private double computeScale() {
    	double containerWidth = mainContainer.getWidth();
    	double containerHeight = mainContainer.getHeight();
    	if(containerWidth <= 0 ||  containerHeight <= 0) {
    		return 1.0;
    	}
    	double scaleX = containerWidth / DESIGN_WIDTH;
    	double scaleY = containerHeight / DESIGN_HEIGHT;
    	return Math.min(scaleX, scaleY);
    }
    
    
    // - Tile Setup -------------------------------------------------------------------------------------------------
    
    public void setupTiles() {
    	if (boardGroup == null) return; 
        registerPolygonsUnder(boardGroup);
    }
    
    private void registerPolygonsUnder(javafx.scene.Parent parent) {
    	for(var node : parent.getChildrenUnmodifiable()) {
    		if(node instanceof Polygon polygon) {
    			tryRegisterPolygon(polygon);
    		}
    		else if(node instanceof javafx.scene.Parent child) {
    			registerPolygonsUnder(child);
    		}
    	}
    }
    
    private void tryRegisterPolygon(Polygon polygon) {
    	String fxId = polygon.getId();
    	if(fxId == null || fxId.isBlank()) return;
    	Tile tile = board.getTile(fxId);
    	if(tile == null) return;
    	ShapeEnum shape = resolveShape(fxId);
    	polygon.setUserData(tile);
    	polygon.setFill(defaultColourFor(shape));
    	polygon.setOnMouseClicked(this::handleTileClick);
    	tileMap.put(fxId, tile);
    	polygonMap.put(fxId, polygon);
    }

    private static ShapeEnum resolveShape(String fxId) {
    	return fxId.length() <= 3 ? ShapeEnum.OCTAGON : ShapeEnum.RHOMBUS;
    }

     
    // - Handle Human Move ------------------------------------------------------------------------------------------
    
    @FXML
    public void handleTileClick(MouseEvent event) {
        if (botThinking) return;
        if (!(event.getSource() instanceof Polygon clicked)) return;
        Tile tile = tileFromPolygon(clicked);
        if (tile == null || !tile.isEmpty()) return;
        boolean movePlayed = gameManager.makeMove(new Move(tile.getCoord(), tile.getShape()));
        if (!movePlayed) return;
        if (strategyVisible) hideStrategy();
        updateTileUI(tile, clicked);
        if(checkAndHandleGameOver()) return;
        updateTurnLabel();
        updatePieRuleButtonVisibility();
        Platform.runLater(this::triggerBotIfNeeded);
    }
    
    
    // - Pie Rule ---------------------------------------------------------------------------------------------------
    
    /*
     * Handles the pie rule: transfers Black's first tile to White 
     * 	then switches turn to Black
     */
    @FXML
    public void handlePieRule(ActionEvent event) {
        	
    	if(isPieRuleUsed() || gameManager.getMoveCount() != 1) return;
    	setPieRuleUsed(true); 
    	transferFirstTileToWhite();
    	gameManager.switchTurn();
    	if(strategyVisible) hideStrategy();
    	updateTurnLabel();
    	Platform.runLater(this::triggerBotIfNeeded);
    }
    
    private void transferFirstTileToWhite() {
    	Tile firstTile = gameManager.getFirstMoveTile();
    	if(firstTile == null) return;
    	firstTile.setOwner(PlayerEnum.WHITE);
    	repaintTile(firstTile);
    }
    
    public void updatePieRuleButtonVisibility() {
    	if(pieRuleButton != null) {
    		pieRuleButton.setVisible(shouldShowPieRuleButton());
    	}
    }
    
    /* Returns true only when pie rule is still available to use */
    public boolean shouldShowPieRuleButton() {
    	return gameManager.getMoveCount() == 1 && !pieRuleUsed;
    }


    // - Turn Display -----------------------------------------------------------------------------------------------
    
    private void updateTurnLabel() {
    	PlayerEnum currentTurn = gameManager.getCurrentTurn();
    	boolean isBlack = currentTurn == PlayerEnum.BLACK;
    	Color playerColour = isBlack? COLOUR_PLAYER_BLACK : COLOUR_PLAYER_WHITE;
    	turnLabel.setText(isBlack? "BLACK'S TURN" : "WHITE'S TURN");
    	turnLabel.setTextFill(playerColour);
    	turnOct.setFill(playerColour);
    	turnRhom.setFill(playerColour);
    }

    
    // - Reset ------------------------------------------------------------------------------------------------------

    @FXML
    private void handleReset() {
        resetGame();
    }


    public void resetGame() {
        hideStrategy();
        gameManager.resetGame();
        pieRuleUsed = false;
        updatePieRuleButtonVisibility();
        restoreAllTileColours();
        startTimer(); 
        updateTurnLabel();
        Platform.runLater(this::triggerBotIfNeeded);
    }
    
    private void restoreAllTileColours() {
    	polygonMap.forEach((id, poly) -> {
    		Tile tile = (Tile) poly.getUserData();
    		if(tile != null) poly.setFill(defaultColourFor(tile.getShape()));
    	});

    }
    
    
    // - Bot Move ---------------------------------------------------------------------------------------------------

    private void triggerBotIfNeeded() {
        if (shouldShowPieRuleButton()) return;
        if (gameManager.isGameOver()) return;
        if (gameManager.getCurrentTurn() != BOT_COLOUR) return;
        botThinking = true;
        scheduleBotMove();
    }

    private void scheduleBotMove() {
    	PauseTransition pause = new PauseTransition(Duration.seconds(BOT_MOVE_DELAY_SECONDS));
    	pause.setOnFinished(e->executeBotMoveIfStillTurn());
    	pause.play();
    }
    
    private void executeBotMoveIfStillTurn() {
    	if(gameManager.getCurrentTurn() != BOT_COLOUR) {
    		botThinking = false;
    		return;
    	}
    	makeBotMove();
    }
    
    private void makeBotMove() {
    	Tile chosenTile = chooseBotTile();
    	if(chosenTile == null) {
    		botThinking = false;
    		return;
    	}
    	boolean movePlayed = gameManager.makeMove(new Move(chosenTile.getCoord(), chosenTile.getShape()));
    	if(!movePlayed) {
    		botThinking = false;
    		Platform.runLater(this::triggerBotIfNeeded);
    	}
    	repaintTile(chosenTile);
    	if(checkAndHandleGameOver()) {
    		botThinking = false;
    		return;
    	}
    	updateTurnLabel();
    	updatePieRuleButtonVisibility();
    	botPlayer.clearCache();
    	botThinking = false;
		Platform.runLater(this::triggerBotIfNeeded);
    }
    
    private Tile chooseBotTile() {
    	BotPlayer.StrategyResult strategy = 
    			botPlayer.computeStrategy(tileMap, BOT_COLOUR, gameManager.getMoveCount());
    	if(strategy != null) {
    		botPlayer.setLastExecutedStrategy(strategy);
    		if(strategy.chosenTile != null) return strategy.chosenTile;
    	}
    	return fallbackBotTile();
    }
    
    private Tile fallbackBotTile() {
    	return tileMap.values().stream()
    			.filter(t->t.isEmpty() && t.getShape() == ShapeEnum.OCTAGON)
    			.findFirst()
    			.orElse(null);
    }
    
    
    // - Strategy Overlay -------------------------------------------------------------------------------------------

    @FXML
    public void showStrat(ActionEvent event) {
        if (strategyVisible) {
            hideStrategy();
            return;
        }
        BotPlayer.StrategyResult strategy = resolveStrategy();
        if (strategy == null) return;
        currentStrategyPath = buildCombinedPath(strategy);
        applyStrategyHighlights(strategy);
        showStrategyPanel(buildStrategyExplanation());
        showStratButton.setText("HIDE STRATEGY");
        strategyVisible = true;
    }
    
    private BotPlayer.StrategyResult resolveStrategy() {
    	BotPlayer.StrategyResult strategy = botPlayer.getCachedStrategy();
    	if(strategy == null) strategy = botPlayer.getLastExecutedStrategy();
    	return strategy;
    }

    private List<Tile> buildCombinedPath(BotPlayer.StrategyResult strategy) {
    	List<Tile> combined = new ArrayList<>(strategy.path);
    	for(Tile t : strategy.opponentPath) {
    		if(!combined.contains(t)) combined.add(t);
    	}
    	return combined;
    }
    
    private void applyStrategyHighlights(BotPlayer.StrategyResult strategy) {
    	Set<String> botCoords = coordSetOf(strategy.path);
    	Set<String> opponentCoords = coordSetOf(strategy.opponentPath);
    	for(Tile tile : currentStrategyPath) {
    		Polygon poly = polygonMap.get(tile.getCoord());
    		if(poly == null) continue;
    		highlightStrategyTile(poly, tile, strategy.chosenTile, botCoords, opponentCoords);
    	}
    }
    
    private void highlightStrategyTile(Polygon poly, Tile tile, Tile chosenTile, Set<String> botCoords, Set<String> opponentCoords) {
    	if(chosenTile != null && tile.getCoord().equals(chosenTile.getCoord())) {
    		poly.setFill(STRATEGY_LAST_TILE_COLOUR);
    	}
    	else if(!tile.isEmpty()) {
    		
    	}
    	else if(botCoords.contains(tile.getCoord())) {
    		poly.setFill(STRATEGY_PATH_COLOUR);
    	}
    	else if(opponentCoords.contains(tile.getCoord())) {
    		poly.setFill(STRATEGY_BLOCK_COLOUR);
    	}
    }
    
    private void showStrategyPanel(String explanation) {
    	if(strategyLabelTitle != null) strategyLabelTitle.setVisible(true);
    	if(strategyLabelText != null) {
    		strategyLabelText.setText(explanation);
    		strategyLabelText.setWrapText(true);
    	}
    	if(strategyScrollPane != null) {
    		strategyScrollPane.setVisible(true);
    		strategyScrollPane.setManaged(true);
    	}
    }
    
    private String buildStrategyExplanation() {
    	if(pieRuleButton.isVisible()) {
    		return "Bot chooses centre tile on its first move as it is generally "
                    + "seen as an optimal starting position.";
    	}
    	return "The bot uses Dijkstra's Algorithm to calculate the best path, "
        + "treating all tiles as connected nodes with different costs: connected tiles (0), "
        + "empty rhombuses (1) (so they're preferred), empty octagons (2), and opponent tiles "
        + "are blocked. It follows the lowest-cost path, but if the opponent is close to winning, "
        + "it switches to a blocking strategy by placing a tile on the opponent's best path to slow "
        + "them down. Otherwise, it focuses on completing its own path as efficiently as possible. "
        + "Orange: BLACK's path. Red: WHITE's path. Yellow: Last move.";
    }

    private void hideStrategy() {
        for (Tile tile : currentStrategyPath) {
            Polygon poly = polygonMap.get(tile.getCoord());
            if (poly == null) continue;
            poly.setFill(tile.isEmpty() ? defaultColourFor(tile.getShape()) : playerColourFor(tile.getOwner()));
        }
        if(strategyLabelTitle != null) strategyLabelTitle.setVisible(false);
        if(strategyLabelText != null) strategyLabelText.setText("");
        if(strategyScrollPane != null) {
        	strategyScrollPane.setVisible(false);
        	strategyScrollPane.setManaged(false);
        }
        if(showStratButton != null) showStratButton.setText("SHOW STRATEGY");
        currentStrategyPath = new ArrayList<>();
        strategyVisible = false;
    }
    
    
    // - Timer ------------------------------------------------------------------------------------------------------

    private void startTimer() {
    	elapsedSeconds = 0;
    	updateTimerLabel();
    	if(gameTimer != null) gameTimer.stop();
    	gameTimer = new Timeline (
    			new KeyFrame(Duration.seconds(1), e-> {
    				elapsedSeconds++;
    				updateTimerLabel();
    		})	
    	);
    	gameTimer.setCycleCount(Animation.INDEFINITE);
    	gameTimer.play();
    }
    
    private void stopTimer() {
    	if(gameTimer != null) gameTimer.stop();
    }
    
    private void updateTimerLabel() {
        if(timerLabel == null) return;
    	int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
    
    
    // - Private Helpers --------------------------------------------------------------------------------------------

    private boolean checkAndHandleGameOver() {
    	if(!gameManager.isGameOver()) return false;
    	turnLabel.setText(gameManager.getCurrentTurn() + " WINS!");
    	stopTimer();
    	return true;
    }
    
    private void updateTileUI(Tile tile, Polygon polygon) {
    	polygon.setFill(playerColourFor(tile.getOwner()));
    }
    
    private void repaintTile(Tile tile) {
    	Polygon poly = polygonMap.get(tile.getCoord());
    	if(poly != null) {
    		updateTileUI(tile, poly);
    	}
    }
    
    private static Tile tileFromPolygon(Polygon polygon) {
    	return(polygon.getUserData() instanceof Tile t) ? t : null;
    }
    
    private static Color playerColourFor(PlayerEnum player) {
    	return player == PlayerEnum.BLACK ? COLOUR_PLAYER_BLACK : COLOUR_PLAYER_WHITE;
    }
    
    private static Color defaultColourFor(ShapeEnum shape) {
    	return shape == ShapeEnum.OCTAGON ? COLOUR_OCTAGON_DEFAULT : COLOUR_RHOMBUS_DEFAULT;
    }
    
    private static Set<String> coordSetOf(List<Tile> tiles) {
    	Set<String> coords = new HashSet<>();
    	for(Tile t : tiles) coords.add(t.getCoord());
    	return coords; 
    }
    
    
    // - Getters / Setters ------------------------------------------------------------------------------------------

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
    
    public void setTurnLabel(Label label) {
        this.turnLabel = label;
    }

    public void setTurnOct(Polygon oct) {
        this.turnOct = oct;
    }

    public void setTurnRhom(Polygon rhom) {
        this.turnRhom = rhom;
    }
    
    public void setPieRuleButton(Button pieRuleButton) {
    	this.pieRuleButton = pieRuleButton;
    }
    
    public void setPieRuleUsed(boolean pieRuleUsed) {
		this.pieRuleUsed = pieRuleUsed;
		updatePieRuleButtonVisibility();
	}
    
    public void setStrategyLabelTitle(Label label) {
        this.strategyLabelTitle = label;
    }

    public void setStrategyLabelText(Label label) {
        this.strategyLabelText = label;
    }

    public void setShowStratButton(Button button) {
        this.showStratButton = button;
    }
    
    public void setGameTimer(javafx.animation.Timeline timer) {
		this.gameTimer = timer;
	}
    
    public void setBotPlayer(BotPlayer botPlayer) {
        this.botPlayer = botPlayer;
    }

    public void setStrategyScrollPane(javafx.scene.control.ScrollPane pane) {
        this.strategyScrollPane = pane;
    }

	public void setTimerLabel(Label label) {
		this.timerLabel = label;		
	}
	
	public void setElapsedSeconds(int seconds) {
		this.elapsedSeconds = seconds;
	}

    public static double getDESIGN_WIDTH() {
		return DESIGN_WIDTH;
	}
	
	public static double getDESIGN_HEIGHT() {
		return DESIGN_HEIGHT;
	}
    
    public Label getTurnLabel() {
    	return turnLabel;
    }

    public Node getPieRuleButton() {
		return pieRuleButton;
	}

    public List<Tile> getCurrentStrategyPath() {
        return currentStrategyPath;
    }

    public int getElapsedSeconds() { 
		return elapsedSeconds; 
	}
		
	public Timeline getGameTimer() { 
		return gameTimer; 
	}

    public GameManager getGameManager() { return gameManager; }

	public boolean isPieRuleUsed() {
		return pieRuleUsed;
	}

	boolean isBotTurn() {
        return gameManager.getCurrentTurn() == BOT_COLOUR;
    }
	
	// Test only - timer methods
	
	public void updateTimerLabelForTest() {
		updateTimerLabel();
	}
	
	public void startTimerForTest() { 
		startTimer(); 
	}
	
	public void stopTimerForTest()  { 
		stopTimer(); 
	}

}
