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

public class Hoofdscherm {

    @FXML private Button btnScrumBoard;
    @FXML private Button btnReports;
    @FXML private Button btnLogout;
    @FXML private Button btnExit;
    @FXML private Label lblWelcome;

    // Huidige geselecteerde Scrum element (Epic, UserStory, Task)
    private Database.scrumdb currentSelection;

    @FXML
    public void initialize() {
        // Stel het welkomstbericht dynamisch in met de huidige gebruikersnaam van het OS
        lblWelcome.setText("Welkom, " + System.getProperty("user.name", "gebruiker"));
    }

    @FXML
    private void switchToScrumBoard(ActionEvent event) throws IOException {
        // Navigeer naar ScrumBoard (teamflow.fxml)
        // Als we van ScrumBoard naar andere schermen moeten navigeren, kunnen we extra data doorgeven via de URL of context
        switchToScene("/com/example/chatapplicatie/teamflow.fxml", event);
    }

    @FXML
    private void switchToReports(ActionEvent event) throws IOException {
        // Open het rapporten scherm
        switchToScene("/com/example/chatapplicatie/reports.fxml", event);
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        // Log uit en ga terug naar het login-scherm
        switchToScene("/com/example/chatapplicatie/login.fxml", event);
    }

    @FXML
    private void handleExit(ActionEvent event) {
        // Sluit de applicatie af
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Hulpmethode om van scene te wisselen
     *
     * @param fxmlPath pad naar het FXML-bestand
     * @param event    de action event die de switch triggert
     * @throws IOException als het FXML-bestand niet gevonden wordt
     */
    private void switchToScene(String fxmlPath, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Deze methode wordt gebruikt om het geselecteerde Scrum element door te geven aan het ScrumBoard
    public void setCurrentSelection(Database.scrumdb currentSelection) {
        this.currentSelection = currentSelection;
    }

    // Event handler voor het klikken op een Epic/User Story/Task
    public void onScrumElementSelected(Database.scrumdb selectedElement) {
        setCurrentSelection(selectedElement);

        // Zodra een element geselecteerd is, kunnen we de chat weer zichtbaar maken, als het een bericht heeft
        if (currentSelection != null) {
            // Chat zichtbaar maken (indien van toepassing)
            // Dit kan via een controller, bijvoorbeeld door een methode aan te roepen in teamflowController
            System.out.println("Geselecteerd element: " + currentSelection.getTitle());
        }
    }
}
