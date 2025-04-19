package com.example.chatapplicatie;

import javafx.fxml.FXML;
import javafx.scene.control.*;
public class AddContactController {
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private Button btnSaveContact;

    @FXML
    private void handleSaveContact() {
        String n = nameField.getText().trim();
        String p = phoneField.getText().trim();
        if (!n.isEmpty() && !p.isEmpty()) {
            System.out.println("Contact: " + n + " - " + p);
            nameField.clear();
            phoneField.clear();
        }
    }
}
