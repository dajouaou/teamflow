package com.example.chatapplicatie;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class StartController {

    @FXML
    private Button btnStartChat;

    @FXML
    private void handleStartChat() {
        openChat();
    }

    private void openChat() {
        try {
            Parent chatRoot = FXMLLoader.load(getClass().getResource("/com/example/chatapplicatie/chat.fxml"));
            Stage stage = (Stage) btnStartChat.getScene().getWindow();
            stage.setScene(new Scene(chatRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
