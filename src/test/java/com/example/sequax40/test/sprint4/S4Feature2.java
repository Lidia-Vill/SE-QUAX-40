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

        // Basic UI setup
        controller.setMainContainer(new StackPane());
        controller.setWindowContainer(new HBox());
        controller.setMasterGroup(new Group());
        controller.setBoardGroup(new Group());

        controller.setBoard(board);
        controller.setGameManager(manager);

        // Strategy UI elements
        strategyLabel1 = new Label();
        strategyLabel2 = new Label();
        showStratButton = new Button();

        controller.setStrategyLabel1(strategyLabel1);
        controller.setStrategyLabel2(strategyLabel2);
        controller.setShowStratButton(showStratButton);
    }


    // Test that show Strategy button
    @Test
    void testShowStrategyTogglesVisibility() {

        setupFakeStrategy();

        controller.showStrat(null);
        assertEquals("HIDE STRATEGY", showStratButton.getText());

        controller.showStrat(null);
        assertEquals("SHOW STRATEGY", showStratButton.getText());
    }


    // Path is cleared when show strategy button is not clicked
    @Test
    void testStrategyPathClearedOnHide() {

        setupFakeStrategy();

        controller.showStrat(null);
        controller.showStrat(null);

        assertTrue(controller.getCurrentStrategyPath().isEmpty());
    }


    // Uses cached strategy first
    @Test
    void testUsesCachedStrategyFirst() {

        BotPlayer bot = new BotPlayer();

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", new Polygon());

        BotPlayer.StrategyResult cached =
                new BotPlayer.StrategyResult(List.of(t), false, t);

        bot.cacheStrategy(cached);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertFalse(controller.getCurrentStrategyPath().isEmpty());
    }


    // Falls back to last executed strategy
    @Test
    void testFallsBackToLastExecutedStrategy() {

        BotPlayer bot = new BotPlayer();

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", new Polygon());

        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(t), false, t);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertFalse(controller.getCurrentStrategyPath().isEmpty());
    }


    // Strategy explanation (blocking)
    @Test
    void testStrategyExplanationBlocking() {

        BotPlayer bot = new BotPlayer();

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", new Polygon());

        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(t), true, t);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertTrue(strategyLabel2.getText().toLowerCase().contains("blocking"));
    }


    //  Labels become visible
    @Test
    void testStrategyLabelsBecomeVisible() {

        setupFakeStrategy();

        controller.showStrat(null);

        assertTrue(strategyLabel1.isVisible());
        assertTrue(strategyLabel2.isVisible());
    }


    //  Labels hidden after hide

    @Test
    void testStrategyLabelsHiddenAfterHide() {

        setupFakeStrategy();

        controller.showStrat(null);
        controller.showStrat(null);

        assertFalse(strategyLabel1.isVisible());
        assertFalse(strategyLabel2.isVisible());
    }


    //Chosen tile highlighted (yellow)
    @Test
    void testChosenTileHighlighted() {

        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        Polygon poly = new Polygon();

        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", poly);

        BotPlayer bot = new BotPlayer();
        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(t), false, t);

        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);

        controller.showStrat(null);

        assertEquals(Color.web("#ffeb3b"), poly.getFill());
    }


    // Helper method to reduce repetition
    private void setupFakeStrategy() {

        Tile t1 = new Tile("A1", ShapeEnum.OCTAGON);
        Tile t2 = new Tile("A2", ShapeEnum.OCTAGON);

        controller.tileMap.put("A1", t1);
        controller.tileMap.put("A2", t2);

        controller.polygonMap.put("A1", new Polygon());
        controller.polygonMap.put("A2", new Polygon());

        BotPlayer bot = new BotPlayer();

        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(Arrays.asList(t1, t2), false, t2);

        bot.setLastExecutedStrategy(strategy);

        controller.setBotPlayer(bot);
    }
}