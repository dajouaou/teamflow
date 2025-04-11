package com.example.chatapplicatie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class teamflowController {

    @FXML
    private Button btnUserStory1;

    @FXML
    private void switchToUI(ActionEvent event) throws IOException {
        System.out.println("Knop gedrukt, switchen naar UI.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chatapplicatie/UI.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("User Story1 Chat");
        stage.show();
    }
}
