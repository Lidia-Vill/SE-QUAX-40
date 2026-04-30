package com.example.sequax40.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class QuaxApplication extends Application {
	
	private static final String FXML_PATH = "/com/example/sequax40/board-view.fxml";
	private static final String WINDOW_TITLE = "Welcome to QUAX-11!";
	
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(QuaxApplication.class.getResource(FXML_PATH)); //load the board made in our fxml file
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMaximized(true);
        stage.setTitle(WINDOW_TITLE); 
        stage.setScene(scene);
        stage.show();
    }
}
