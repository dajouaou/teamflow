package com.example.chatapplicatie;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TeamFlow");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);

    }

    public static void main(String[] args) {
        launch();
    }
}