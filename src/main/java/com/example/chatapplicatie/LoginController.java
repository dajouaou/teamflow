package com.example.chatapplicatie;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // Gebruikers en wachtwoorden
    private final Map<String, String> users = new HashMap<>();

    @FXML
    public void initialize() {
        // Gebruikers toevoegen met wachtwoord
        users.put("admin", "admin123");
        users.put("user", "user123");
        errorLabel.setVisible(false);
    }

    @FXML
    private void requestPasswordFocus() {
        passwordField.requestFocus();
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Vul gebruikersnaam en wachtwoord in", Color.RED);
            return;
        }

        // Controleer of gebruiker bestaat én wachtwoord klopt
        boolean ok = users.containsKey(user) && users.get(user).equals(pass);

        if (!ok) {
            showError("Ongeldige inloggegevens", Color.RED);
            return;
        }

        // Inloggen geslaagd, laad hoofdscherm
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/chatapplicatie/hoofdscherm.fxml")
            );
            Parent root = loader.load();
            Stage current = (Stage) usernameField.getScene().getWindow();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("TeamFlow");
            newStage.setMaximized(current.isMaximized());
            current.close();
            newStage.show();
            Platform.runLater(() -> newStage.setMaximized(true));

        } catch (IOException e) {
            showError("Kon hoofdscherm niet laden", Color.RED);
            e.printStackTrace();
        }
    }

    private void showError(String msg, Color c) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(c);
        errorLabel.setVisible(true);
    }
}
