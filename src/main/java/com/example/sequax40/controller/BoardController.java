package com.example.sequax40.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import com.example.sequax40.model.board.Board;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;

public class BoardController { 

    @FXML private StackPane gameBoardStackPane;
    @FXML private Group boardGroup;
    
    private static final Color SELECTED_COLOR = Color.WHITE;


    @FXML private StackPane mainContainer; 
    @FXML private Group masterGroup;      

    private final double DESIGN_WIDTH = 900.0; 
    private final double DESIGN_HEIGHT = 850.0;

    @FXML
    public void initialize() {

        masterGroup.setManaged(true); 
        masterGroup.setTranslateY(-90);

        StackPane.setAlignment(masterGroup, javafx.geometry.Pos.TOP_CENTER);

        NumberBinding scaleBinding = Bindings.createDoubleBinding(() -> {
            double containerWidth = mainContainer.getWidth();
            double containerHeight = mainContainer.getHeight();
            
            if (containerWidth <= 0 || containerHeight <= 0) return 1.0;

            double scaleX = containerWidth / DESIGN_WIDTH;
            double scaleY = containerHeight / DESIGN_HEIGHT;

            return Math.min(scaleX, scaleY);
        }, mainContainer.widthProperty(), mainContainer.heightProperty());

        masterGroup.scaleXProperty().bind(scaleBinding);
        masterGroup.scaleYProperty().bind(scaleBinding);
    }

    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();


        Color originalColor = (Color) clicked.getUserData();


        if (originalColor == null) {
            clicked.setUserData(clicked.getFill());
            clicked.setFill(SELECTED_COLOR);
        } else {

            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor);
            } else {
                clicked.setFill(SELECTED_COLOR);
            }
        }
    }
}
/*
public class BoardController { // Keep your actual class name!

    @FXML private StackPane gameBoardStackPane;
    @FXML private Group boardGroup;

    // YOUR EXACT BOARD SIZE (From Scene Builder)
    private final double CONTENT_WIDTH = 1050.0;
    private final double CONTENT_HEIGHT = 1050.0;
    
    private static final Color SELECTED_COLOR = Color.WHITE;

    @FXML
    public void initialize() {
        // --- 1. THE RESIZE FIX (Using Bindings) ---
        
        // This calculates the scale ratio automatically:
        // Math.min(WindowWidth / 1050, WindowHeight / 1050)
        NumberBinding scaleBinding = Bindings.createDoubleBinding(() -> {
            double containerWidth = gameBoardStackPane.getWidth();
            double containerHeight = gameBoardStackPane.getHeight();
            
            if (containerWidth == 0 || containerHeight == 0) return 1.0;

            double scaleX = containerWidth / CONTENT_WIDTH;
            double scaleY = containerHeight / CONTENT_HEIGHT;
            return Math.min(scaleX, scaleY);
        }, gameBoardStackPane.widthProperty(), gameBoardStackPane.heightProperty());

        // Bind the scale of the Group to this calculation
        boardGroup.scaleXProperty().bind(scaleBinding);
        boardGroup.scaleYProperty().bind(scaleBinding);

        // --- 2. THE CENTERING FIX ---
        // This keeps the board valid even when shrunk
        // Managed=false allows the Group to ignore layout rules and just float in the center
        boardGroup.setManaged(false); 
        
        // Force the Group to stay in the exact center of the StackPane
        boardGroup.layoutXProperty().bind(
            gameBoardStackPane.widthProperty().subtract(CONTENT_WIDTH).divide(2)
        );
        boardGroup.layoutYProperty().bind(
            gameBoardStackPane.heightProperty().subtract(CONTENT_HEIGHT).divide(2)
        );
    }

    // --- 3. THE CRASH FIX ---
    // This is the missing method that was causing the "Exception in Application start method"
    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();


        Color originalColor = (Color) clicked.getUserData();


        if (originalColor == null) {
            clicked.setUserData(clicked.getFill());
            clicked.setFill(SELECTED_COLOR);
        } else {

            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor);
            } else {
                clicked.setFill(SELECTED_COLOR);
            }
        }
    }
}
*/
/*public class BoardController {

    @FXML private StackPane gameBoardStackPane;
    @FXML private Group boardGroup;

    // YOUR EXACT DIMENSIONS
    private final double CONTENT_WIDTH = 1050.0;
    private final double CONTENT_HEIGHT = 1050.0;
    
private static final Color SELECTED_COLOR = Color.WHITE;


    
    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();


        Color originalColor = (Color) clicked.getUserData();


        if (originalColor == null) {
            clicked.setUserData(clicked.getFill());
            clicked.setFill(SELECTED_COLOR);
        } else {

            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor);
            } else {
                clicked.setFill(SELECTED_COLOR);
            }
        }
    }


    @FXML
    public void initialize() {
        // 1. Create a Scale transform object and add it to the Group
        // This is often more stable than setScaleX/Y for Groups
        Scale scale = new Scale(1, 1);
        boardGroup.getTransforms().add(scale);

        // 2. Define the resize logic
        Runnable resizeHandler = () -> {
            double windowWidth = gameBoardStackPane.getWidth();
            double windowHeight = gameBoardStackPane.getHeight();

            // Safety check
            if (windowWidth <= 0 || windowHeight <= 0) return;

            // Calculate scale factors
            // Example: Window Height (768) / Board Height (1050) = 0.73
            double scaleX = windowWidth / CONTENT_WIDTH;
            double scaleY = windowHeight / CONTENT_HEIGHT;

            // Use the smaller scale to ensure the ENTIRE board fits
            double scaleFactor = Math.min(scaleX, scaleY);
            
            // Apply the scale
            // Pivot at (0,0) ensures it scales from top-left, 
            // but StackPane will center the resulting smaller object automatically.
            scale.setX(scaleFactor);
            scale.setY(scaleFactor);
            scale.setPivotX(0); 
            scale.setPivotY(0); 
            
            // Force the Group to report its new size to the parent StackPane
            // so the StackPane can center it correctly
            boardGroup.resize(CONTENT_WIDTH * scaleFactor, CONTENT_HEIGHT * scaleFactor);
        };

        // 3. Add listeners
        gameBoardStackPane.widthProperty().addListener((obs, old, val) -> resizeHandler.run());
        gameBoardStackPane.heightProperty().addListener((obs, old, val) -> resizeHandler.run());

        // 4. Run once after the window loads
        Platform.runLater(resizeHandler);
    }
}
*/
/*
public class BoardController { 

	  @FXML private StackPane gameBoardStackPane;
      @FXML private Group boardGroup;

      // REPLACE THESE NUMBERS with the size you found in Scene Builder!
      private final double CONTENT_WIDTH = 1050.0; 
      private final double CONTENT_HEIGHT = 1050.0;

      @FXML
      public void initialize() {
          // 1. Listen for window resize events
          gameBoardStackPane.widthProperty().addListener((obs, oldVal, newVal) -> resizeBoard());
          gameBoardStackPane.heightProperty().addListener((obs, oldVal, newVal) -> resizeBoard());

          // 2. Wait for the window to actually open, then resize
          Platform.runLater(() -> resizeBoard());
      }

  
    private static final Color SELECTED_COLOR = Color.WHITE;


    
    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();


        Color originalColor = (Color) clicked.getUserData();


        if (originalColor == null) {
            clicked.setUserData(clicked.getFill());
            clicked.setFill(SELECTED_COLOR);
        } else {

            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor);
            } else {
                clicked.setFill(SELECTED_COLOR);
            }
        }
    }


    // ... inside your controller class ...

      
        private void resizeBoard() {
            double windowWidth = gameBoardStackPane.getWidth();
            double windowHeight = gameBoardStackPane.getHeight();

            // Safety check: if window is closed, stop
            if (windowWidth <= 0 || windowHeight <= 0) return;

            // --- THE MATH ---
            // Example: If Window is 1000px and Board is 2000px
            // 1000 / 2000 = 0.5 (This will shrink the board to 50%)
            double scaleX = windowWidth / CONTENT_WIDTH;
            double scaleY = windowHeight / CONTENT_HEIGHT;

            // Use the smaller scale to ensure the ENTIRE board fits
            double scaleFactor = Math.min(scaleX, scaleY);

            // Apply the shrinking
            boardGroup.setScaleX(scaleFactor);
            boardGroup.setScaleY(scaleFactor);
            
            // Center the group (Fixes the "Top Left" issue)
            // Groups scale from the center by default, but sometimes drift.
            // This ensures it stays in the middle of the StackPane.
            boardGroup.setTranslateX(0); 
            boardGroup.setTranslateY(0);
        }
}*/
/*
import com.example.sequax40.model.board.Board;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class BoardController {

    private static final Color SELECTED_COLOR = Color.WHITE;

    private Board board;

    @FXML
    private GridPane boardGrid;

    public void initialize() {
        board = new Board(11, 11);

    }

    @FXML
    private void handleCellClick(MouseEvent event) {

        Polygon clicked = (Polygon) event.getSource();


        Color originalColor = (Color) clicked.getUserData();


        if (originalColor == null) {
            clicked.setUserData(clicked.getFill());
            clicked.setFill(SELECTED_COLOR);
        } else {

            if (clicked.getFill().equals(SELECTED_COLOR)) {
                clicked.setFill(originalColor);
            } else {
                clicked.setFill(SELECTED_COLOR);
            }
        }
    }
}*/