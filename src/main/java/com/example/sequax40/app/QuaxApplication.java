package com.example.sequax40.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class QuaxApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(QuaxApplication.class.getResource("/com/example/sequax40/board-view.fxml")); //load the board made in our fxml file
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMaximized(true);
        stage.setTitle("Welcome to QUAX-11!"); //set the title
        stage.setScene(scene);
        stage.show();
    }
}
