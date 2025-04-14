package com.example.chatapplicatie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hoofdscherm {
        @FXML private Button btnScrumBoard;
        @FXML private Button btnReports;
        @FXML private Button btnLogout;
        @FXML private Button btnExit;
        @FXML private Label lblWelcome;

        @FXML
        public void initialize() {
            // Stel welkomstbericht in (kan dynamisch gemaakt worden met gebruikersnaam)
            lblWelcome.setText("Welkom, " + System.getProperty("user.name", "gebruiker"));
        }

        @FXML
        private void switchToScrumBoard(ActionEvent event) throws IOException {
            switchToScene("/com/example/chatapplicatie/teamflow.fxml", event);
        }

        @FXML
        private void switchToReports(ActionEvent event) throws IOException {
            switchToScene("/com/example/chatapplicatie/reports.fxml", event);
        }

        private void switchToScene(String fxmlPath, ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

        @FXML
        private void handleLogout(ActionEvent event) throws IOException {
            // Terug naar login scherm
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/chatapplicatie/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

        @FXML
        private void handleExit(ActionEvent event) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }

    }