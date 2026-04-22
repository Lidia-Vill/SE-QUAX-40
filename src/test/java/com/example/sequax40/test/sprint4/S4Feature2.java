package com.example.sequax40.test.sprint4;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.model.player.BotPlayer;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class S4Feature2 {

    private BoardController controller;
    private Board board;
    private GameManager manager;

    private Label strategyLabel1;
    private Label strategyLabel2;
    private Button showStratButton;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setup() {

        board = new Board(11, 11);
        controller = new BoardController();
        Map<String, Tile> tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);

        controller.setMainContainer(new StackPane());
        controller.setWindowContainer(new HBox());
        controller.setMasterGroup(new Group());
        controller.setBoardGroup(new Group());

        controller.setBoard(board);
        controller.setGameManager(manager);

        strategyLabel1 = new Label();
        strategyLabel2 = new Label();
        showStratButton = new Button();

        controller.setStrategyLabel1(strategyLabel1);
        controller.setStrategyLabel2(strategyLabel2);
        controller.setShowStratButton(showStratButton);

        // prevents NullPointerException on pieRuleButton.isVisible()
        Button pieRuleButton = new Button();
        pieRuleButton.setVisible(false);
        controller.setPieRuleButton(pieRuleButton);

        // prevents NullPointerException on strategyScrollPane
        controller.setStrategyScrollPane(new ScrollPane());
    }


    // Toggle Functionality: button text switches correctly
    @Test
    void testShowStrategyTogglesVisibility() {

        setupFakeStrategy();

        controller.showStrat(null);
        assertEquals("HIDE STRATEGY", showStratButton.getText());

        controller.showStrat(null);
        assertEquals("SHOW STRATEGY", showStratButton.getText());
    }


    // Strategy Path Handling: path cleared on hide
    @Test
    void testStrategyPathClearedOnHide() {

        setupFakeStrategy();

        controller.showStrat(null);
        controller.showStrat(null);

        assertTrue(controller.getCurrentStrategyPath().isEmpty());
    }


    // Strategy Retrieval Logic: cached strategy used first
    @Test
    void testUsesCachedStrategyFirst() {

        BotPlayer bot = new BotPlayer();

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", new Polygon());

        BotPlayer.StrategyResult cached =
                new BotPlayer.StrategyResult(List.of(t), Collections.emptyList(), false, t);

        bot.cacheStrategy(cached);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertFalse(controller.getCurrentStrategyPath().isEmpty());
    }


    // Strategy Retrieval Logic: falls back to last executed strategy
    @Test
    void testFallsBackToLastExecutedStrategy() {

        BotPlayer bot = new BotPlayer();

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", new Polygon());

        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(t), Collections.emptyList(), false, t);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertFalse(controller.getCurrentStrategyPath().isEmpty());
    }


    // Strategy Explanation Display: blocking mode shows correct text
    @Test
    void testStrategyExplanationBlocking() {

        BotPlayer bot = new BotPlayer();

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", new Polygon());

        Tile opp = new Tile("B1", ShapeEnum.OCTAGON);
        controller.tileMap.put("B1", opp);
        controller.polygonMap.put("B1", new Polygon());

        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(t), List.of(opp), true, t);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertTrue(strategyLabel2.getText().toLowerCase().contains("blocking"));
    }


    // Label Visibility: labels become visible when strategy shown
    @Test
    void testStrategyLabelsBecomeVisible() {

        setupFakeStrategy();

        controller.showStrat(null);

        assertTrue(strategyLabel1.isVisible());
        assertTrue(strategyLabel2.isVisible());
    }


    // Label Visibility: labels hidden after strategy toggled off
    @Test
    void testStrategyLabelsHiddenAfterHide() {

        setupFakeStrategy();

        controller.showStrat(null);
        controller.showStrat(null);

        assertFalse(strategyLabel1.isVisible());
        assertFalse(showStratButton.getText().equals("HIDE STRATEGY"));
    }


    // Tile Highlighting: chosen tile highlighted in yellow
    @Test
    void testChosenTileHighlighted() {

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        Polygon poly = new Polygon();

        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", poly);

        BotPlayer bot = new BotPlayer();
        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(t), Collections.emptyList(), false, t);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertEquals(Color.web("#ffeb3b"), poly.getFill());
    }


    // Tile Highlighting: opponent path tiles highlighted in red
    @Test
    void testOpponentPathHighlightedRed() {

        Tile botTile = new Tile("A1", ShapeEnum.OCTAGON);
        Tile oppTile = new Tile("B1", ShapeEnum.OCTAGON);

        Polygon botPoly = new Polygon();
        Polygon oppPoly = new Polygon();

        controller.tileMap.put("A1", botTile);
        controller.tileMap.put("B1", oppTile);
        controller.polygonMap.put("A1", botPoly);
        controller.polygonMap.put("B1", oppPoly);

        BotPlayer bot = new BotPlayer();
        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(botTile), List.of(oppTile), true, botTile);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertEquals(Color.web("#e53935"), oppPoly.getFill());
    }


    // Tile Highlighting: bot path tiles highlighted in orange
    @Test
    void testBotPathHighlightedOrange() {

        Tile botTile = new Tile("A2", ShapeEnum.OCTAGON);
        Tile chosenTile = new Tile("A1", ShapeEnum.OCTAGON);

        Polygon botPoly = new Polygon();
        Polygon chosenPoly = new Polygon();

        controller.tileMap.put("A2", botTile);
        controller.tileMap.put("A1", chosenTile);
        controller.polygonMap.put("A2", botPoly);
        controller.polygonMap.put("A1", chosenPoly);

        BotPlayer bot = new BotPlayer();
        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(
                        List.of(botTile, chosenTile), Collections.emptyList(), false, chosenTile);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertEquals(Color.web("#ff9800"), botPoly.getFill());
    }


    // Tile Highlighting: both paths shown simultaneously
    @Test
    void testBothPathsHighlightedSimultaneously() {

        Tile botTile  = new Tile("A2", ShapeEnum.OCTAGON);
        Tile oppTile  = new Tile("B2", ShapeEnum.OCTAGON);
        Tile chosen   = new Tile("A1", ShapeEnum.OCTAGON);

        Polygon botPoly    = new Polygon();
        Polygon oppPoly    = new Polygon();
        Polygon chosenPoly = new Polygon();

        controller.tileMap.put("A2", botTile);
        controller.tileMap.put("B2", oppTile);
        controller.tileMap.put("A1", chosen);
        controller.polygonMap.put("A2", botPoly);
        controller.polygonMap.put("B2", oppPoly);
        controller.polygonMap.put("A1", chosenPoly);

        BotPlayer bot = new BotPlayer();
        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(
                        List.of(botTile, chosen), List.of(oppTile), true, chosen);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertEquals(Color.web("#ff9800"), botPoly.getFill());
        assertEquals(Color.web("#e53935"), oppPoly.getFill());
        assertEquals(Color.web("#ffeb3b"), chosenPoly.getFill());
    }


    // Helper method
    private void setupFakeStrategy() {

        Tile t1 = new Tile("A1", ShapeEnum.OCTAGON);
        Tile t2 = new Tile("A2", ShapeEnum.OCTAGON);

        controller.tileMap.put("A1", t1);
        controller.tileMap.put("A2", t2);

        controller.polygonMap.put("A1", new Polygon());
        controller.polygonMap.put("A2", new Polygon());

        BotPlayer bot = new BotPlayer();

        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(
                        Arrays.asList(t1, t2), Collections.emptyList(), false, t2);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);
    }
}