package com.example.chatapplicatie;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.*;

public class LinkDialog {
    @FXML private Dialog<ButtonType> linkDialog;
    @FXML private ListView<Database.scrumdb> entityList;
    private Database.Message selectedMessage;
    private Runnable updateCallback;

    public LinkDialog() { initializeDialog(); }

    private void initializeDialog() {
        linkDialog = new Dialog<>();
        linkDialog.setTitle("Koppel Scrum Element");
        linkDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        entityList = new ListView<>();
        linkDialog.getDialogPane().setContent(entityList);
    }

    public void setEntities(List<Database.scrumdb> list) {
        entityList.getItems().setAll(list);
    }
    public void setSelectedMessage(Database.Message m) { this.selectedMessage = m; }
    public void setUpdateCallback(Runnable cb) { this.updateCallback = cb; }

    @FXML
    private void showLinkDialog() {
        Optional<ButtonType> res = linkDialog.showAndWait();
        if (res.filter(b->b==ButtonType.OK).isPresent() && selectedMessage!=null) {
            Database.scrumdb e = entityList.getSelectionModel().getSelectedItem();
            if (e!=null) {
                selectedMessage.setLinkedEntity(e);
                if (updateCallback!=null) updateCallback.run();
            }
        }
    }
}
