package com.example.chatapplicatie;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private List<User> users = new ArrayList<>();

    public void initialize() {
        // Voeg testgebruikers toe
        users.add(new User("admin", "admin123", "Administrator"));
        users.add(new User("user", "user123", "Standaard Gebruiker"));

        // Stel initiële melding in
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    @FXML
    private void requestPasswordFocus() {
        passwordField.requestFocus();
    }

    // Je bestaande handleLogin methode blijft hetzelfde
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Reset melding
        errorLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vul zowel gebruikersnaam als wachtwoord in", Color.RED);
            return;
        }

        User authenticatedUser = authenticate(username, password);

        if (authenticatedUser != null) {
            showError("Login succesvol! Welkom, " + authenticatedUser.getFullName(), Color.GREEN);

            try {
                // Laad het teamflow.fxml bestand
                FXMLLoader loader = new FXMLLoader(getClass().getResource("teamflow.fxml"));
                Parent root = loader.load();

                // Haal het huidige stage object op
                Stage currentStage = (Stage) usernameField.getScene().getWindow();

                // Maak een nieuwe stage aan
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));

                // Zet de titel en andere properties
                newStage.setTitle("TeamFlow");

                // Zet de window state properties van de oude stage over
                newStage.setMaximized(currentStage.isMaximized());
                newStage.setFullScreen(currentStage.isFullScreen());

                // Sluit de oude stage
                currentStage.close();

                // Toon de nieuwe stage
                newStage.show();

                // Forceer maximalisatie als nodig
                Platform.runLater(() -> {
                    newStage.setMaximized(true);
                });

            } catch (IOException e) {
                showError("Kon het hoofdscherm niet laden", Color.RED);
                e.printStackTrace();
            }
        } else {
            showError("Ongeldige gebruikersnaam of wachtwoord", Color.RED);
        }
    }
    private User authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) &&
                        user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
//private
    private void showError(String message, Color color) {
        errorLabel.setText(message);
        errorLabel.setTextFill(color);
        errorLabel.setVisible(true);
    }

    private static class User {
        private final String username;
        private final String password;
        private final String fullName;

        public User(String username, String password, String fullName) {
            this.username = username;
            this.password = password;
            this.fullName = fullName;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getFullName() { return fullName; }
    }
}