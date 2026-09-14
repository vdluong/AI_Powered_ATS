package com.ats.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("AI-POWERED ATS");

        VBox root = new VBox(title);

        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("AI-Powered ATS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}