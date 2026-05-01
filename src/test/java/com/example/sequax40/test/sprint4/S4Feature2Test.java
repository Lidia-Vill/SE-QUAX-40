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
import com.example.sequax40.test.helperMethods.ControllerHelpers;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class S4Feature2Test {

	private ControllerHelpers helper;
    private BoardController controller;
    private Board board;
    private GameManager manager;

    private Label strategyLabelTitle;
    private Label strategyLabelText;
    private Button showStratButton;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setup() {
    	helper = new ControllerHelpers();
        controller = helper.createController();
        board = controller.board;
        manager = controller.getGameManager();

        strategyLabelTitle = controller.getStrategyLabelTitle();
        strategyLabelText  = controller.getStrategyLabelText();
        showStratButton    = controller.getShowStratButton();
    }

    @Test
    void testShowStrategyTogglesVisibility() {
        setupFakeStrategy();
        controller.showStrat(null);
        assertEquals("HIDE STRATEGY", showStratButton.getText());
        controller.showStrat(null);
        assertEquals("SHOW STRATEGY", showStratButton.getText());
    }

    @Test
    void testStrategyPathClearedOnHide() {
        setupFakeStrategy();
        controller.showStrat(null);
        controller.showStrat(null);
        assertTrue(controller.getCurrentStrategyPath().isEmpty());
    }

    @Test
    void testUsesCachedStrategyFirst() {
        BotPlayer bot = new BotPlayer();
        Tile t = new Tile("A1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.polygonMap.put("A1", new Polygon());

        BotPlayer.StrategyResult cached =
                new BotPlayer.StrategyResult(List.of(t), Collections.emptyList(), false, t);
        bot.setLastExecutedStrategy(cached);
        // populate cache by computing (uses setLastExecutedStrategy as fallback in showStrat)
        controller.setBotPlayer(bot);
        controller.showStrat(null);
        assertFalse(controller.getCurrentStrategyPath().isEmpty());
    }

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

    @Test
    void testStrategyExplanationBlocking() {
        BotPlayer bot = new BotPlayer();
        Tile t   = new Tile("A1", ShapeEnum.OCTAGON);
        Tile opp = new Tile("B1", ShapeEnum.OCTAGON);
        controller.tileMap.put("A1", t);
        controller.tileMap.put("B1", opp);
        controller.polygonMap.put("A1", new Polygon());
        controller.polygonMap.put("B1", new Polygon());

        BotPlayer.StrategyResult strategy =
                new BotPlayer.StrategyResult(List.of(t), List.of(opp), true, t);
        bot.setLastExecutedStrategy(strategy);
        controller.setBotPlayer(bot);
        controller.showStrat(null);

        assertTrue(strategyLabelText.getText().toLowerCase().contains("blocking"));
    }

    @Test
    void testStrategyLabelsBecomeVisible() {
        setupFakeStrategy();
        controller.showStrat(null);
        assertTrue(strategyLabelTitle.isVisible());
        assertTrue(strategyLabelText.isVisible());
    }

    @Test
    void testStrategyLabelsHiddenAfterHide() {
        setupFakeStrategy();
        controller.showStrat(null);
        controller.showStrat(null);
        assertFalse(strategyLabelTitle.isVisible());
        assertFalse(showStratButton.getText().equals("HIDE STRATEGY"));
    }

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

    @Test
    void testBotPathHighlightedOrange() {
        Tile botTile    = new Tile("A2", ShapeEnum.OCTAGON);
        Tile chosenTile = new Tile("A1", ShapeEnum.OCTAGON);
        Polygon botPoly    = new Polygon();
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

    @Test
    void testBothPathsHighlightedSimultaneously() {
        Tile botTile = new Tile("A2", ShapeEnum.OCTAGON);
        Tile oppTile = new Tile("B2", ShapeEnum.OCTAGON);
        Tile chosen  = new Tile("A1", ShapeEnum.OCTAGON);
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