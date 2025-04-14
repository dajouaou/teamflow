package com.example.chatapplicatie;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Optional;

public class LinkDialog {
    @FXML
    private Dialog<ButtonType> linkDialog;
    @FXML
    private ListView<Database.scrumdb> entityList;
    private Database.Message currentlySelectedMessage;
    private Runnable updateChatDisplay;

    public LinkDialog() {
        initializeDialog();
    }

    private void initializeDialog() {
        linkDialog = new Dialog<>();
        linkDialog.setTitle("Koppel aan Scrum Entiteit");
        linkDialog.setHeaderText("Selecteer een entiteit om aan te koppelen");

        // Voeg knoppen toe
        linkDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Maak en configureer de ListView
        entityList = new ListView<>();
        linkDialog.getDialogPane().setContent(entityList);
    }

    public void setEntities(List<Database.scrumdb> entities) {
        entityList.getItems().setAll(entities);
    }

    public void setCurrentlySelectedMessage(Database.Message message) {
        this.currentlySelectedMessage = message;
    }

    public void setUpdateChatDisplayCallback(Runnable callback) {
        this.updateChatDisplay = callback;
    }

    @FXML
    void showLinkDialog() {
        if (linkDialog == null) {
            initializeDialog();
        }

        Optional<ButtonType> result = linkDialog.showAndWait();
        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                Database.scrumdb selected = entityList.getSelectionModel().getSelectedItem();
                if (selected != null && currentlySelectedMessage != null) {
                    currentlySelectedMessage.setLinkedEntity(selected);
                    if (updateChatDisplay != null) {
                        updateChatDisplay.run();
                    }
                } else {
                    showAlert("Selectie vereist", "Selecteer eerst een entiteit");
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}