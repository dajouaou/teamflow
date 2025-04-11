package com.example.chatapplicatie;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton; // Zorg dat deze fx:id in je FXML staat

    private final List<User> users = new ArrayList<>();
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    public void initialize() {
        // Testgebruikers toevoegen
        users.add(new User("admin", "admin123", "Administrator"));
        users.add(new User("user", "user123", "Standaard Gebruiker"));

        errorLabel.setOpacity(0);
    }

    @FXML
    private void handleLogin() {
        loginAttempts++;

        if (loginAttempts >= MAX_ATTEMPTS) {
            showError("Te veel pogingen, probeer later opnieuw", Color.RED);
            loginButton.setDisable(true);
            applyShakeAnimation(loginButton);
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vul zowel gebruikersnaam als wachtwoord in", Color.RED);
            applyShakeAnimation(username.isEmpty() ? usernameField : passwordField);
            return;
        }

        User authenticatedUser = authenticate(username, password);

        if (authenticatedUser != null) {
            showError("Login succesvol! Welkom, " + authenticatedUser.fullName(), Color.GREEN);
            applySuccessAnimation(loginButton);
        } else {
            showError("Ongeldige gebruikersnaam of wachtwoord (poging " + loginAttempts + "/" + MAX_ATTEMPTS + ")", Color.RED);
            applyShakeAnimation(passwordField);
        }
    }

    private User authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.username().equals(username) &&
                        user.password().equals(password))
                .findFirst()
                .orElse(null);
    }

    private void showError(String message, Color color) {
        errorLabel.setText(message);
        errorLabel.setTextFill(color);

        FadeTransition ft = new FadeTransition(Duration.millis(500), errorLabel);
        ft.setFromValue(errorLabel.getOpacity());
        ft.setToValue(1.0);
        ft.play();
    }

    private void applyShakeAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(70), node);
        tt.setFromX(0);
        tt.setByX(15);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.setOnFinished(event -> node.setTranslateX(0));
        tt.play();
    }

    private void applySuccessAnimation(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.1);
        st.setToY(1.1);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.setOnFinished(event -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
        st.play();
    }

    // Record class voor onveranderlijke gebruikersdata
    private record User(String username, String password, String fullName) {}
}