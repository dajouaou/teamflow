package com.example.chatapplicatie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;  // Import voor FXMLLoader
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;  // Import voor IOException

public class GeneralScrumBoardController {

    @FXML
    private Button btnGoBack;

    @FXML
    private void goBackToHome() throws IOException {
        // Navigeren terug naar het hoofdscherm
        Stage stage = (Stage) btnGoBack.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/chatapplicatie/hoofdscherm.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
