package com.example.chatapplicatie;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddContactController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private Button btnSaveContact;

    @FXML
    private void handleSaveContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (!name.isEmpty() && !phone.isEmpty()) {
            System.out.println("Nieuw contact toegevoegd: " + name + " - " + phone);
            nameField.clear();
            phoneField.clear();
        }
    }
}
