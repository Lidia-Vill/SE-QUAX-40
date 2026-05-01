package com.example.sequax40.test.helperMethods;

import java.util.HashMap;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.game.GameManager;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;


public class ControllerHelpers {

    static {
        // Ensures JavaFX toolkit is started once
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // already started
        }
    }

    public BoardController createController() {

        BoardController controller = new BoardController();

        StackPane mainContainer = new StackPane();
        HBox windowContainer = new HBox();
        Group masterGroup = new Group();
        Group boardGroup = new Group();

        controller.setMainContainer(mainContainer);
        controller.setWindowContainer(windowContainer);
        controller.setMasterGroup(masterGroup);
        controller.setBoardGroup(boardGroup);

        controller.setTurnLabel(new Label());
        controller.setTurnOct(new Polygon());
        controller.setTurnRhom(new Polygon());
        controller.setPieRuleButton(new Button());
        controller.setTimerLabel(new Label());
        controller.setShowStratButton(new Button());
        controller.setStrategyLabelTitle(new Label());
        controller.setStrategyLabelText(new Label());
        controller.setStrategyScrollPane(new ScrollPane());

        controller.initialize();

        controller.board = new Board(11, 11);
        controller.tileMap = new HashMap<>();
        controller.polygonMap = new HashMap<>();

        controller.setupTiles();

        return controller;
    }

    
}
