package com.example.chatapplicatie;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class teamflowController {

    // UI-elementen
    @FXML private ListView<Database.scrumdb> entityList;  // ListView voor Epics, User Stories en Taken
    @FXML private VBox chatContainer, chatMessagesForEntity, generalChatMessages;
    @FXML private TextField chatInput, generalChatInput;
    @FXML private VBox sprintBacklog, toDo, inProgress, done;

    private final List<Database.Epic> epics = new ArrayList<>();
    private final List<Database.scrumdb> scrumElementen = new ArrayList<>();
    private Database.scrumdb huidige;  // Geselecteerde Scrum element
    private final Database.User currentUser = new Database.User(System.getProperty("user.name"));

    // **Berichten versturen naar de algemene chat**
    @FXML
    private void sendGeneralMessage(ActionEvent e) {
        String txt = generalChatInput.getText().trim();
        if (!txt.isEmpty()) {
            // Maak een bericht zonder gekoppeld Epic/UserStory/Task (geen linkedEntity)
            var msg = new Database.Message(txt, currentUser, LocalDateTime.now(), null);
            updateGeneralChatDisplay(msg);  // Update de algemene chat met het bericht zonder gekoppeld element
            generalChatInput.clear();
        }
    }

    // **Toon berichten in de algemene chat**
    private void updateGeneralChatDisplay(Database.Message msg) {
        Label lbl;

        // Als het bericht is gekoppeld aan een Scrum-element (Epic, UserStory, Task)
        if (msg.getLinkedEntity() != null) {
            String entityInfo = msg.getLinkedEntity() instanceof Database.Epic ? "Epic: " + msg.getLinkedEntity().getTitle() :
                    msg.getLinkedEntity() instanceof Database.UserStory ? "User Story: " + msg.getLinkedEntity().getTitle() :
                            msg.getLinkedEntity() instanceof Database.Task ? "Taak: " + msg.getLinkedEntity().getTitle() :
                                    "Geen Entiteit";
            lbl = new Label(
                    String.format("[%s] %s: %s (Gekoppeld aan: %s)",
                            msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")),
                            msg.getSender().getName(),
                            msg.getContent(),
                            entityInfo)
            );
        } else {
            lbl = new Label(
                    String.format("[%s] %s: %s",
                            msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")),
                            msg.getSender().getName(),
                            msg.getContent())
            );
        }

        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: normal;");
        // Voeg het bericht bovenaan toe in de algemene chat
        generalChatMessages.getChildren().add(0, lbl);  // 0 is de index om het bovenaan de VBox toe te voegen
    }

    // **Berichten versturen en koppelen aan de geselecteerde entiteit (Epic/User Story/Task)**
    @FXML
    private void sendMessage(ActionEvent e) {
        String txt = chatInput.getText().trim();
        if (!txt.isEmpty() && huidige != null) {
            var msg = new Database.Message(txt, currentUser, LocalDateTime.now(), huidige);
            huidige.getLinkedMessages().add(msg);  // Koppel het bericht aan de geselecteerde entiteit
            updateChatDisplay(huidige);
            chatInput.clear();
        }
    }

    // **Chatweergave updaten voor de geselecteerde entiteit**
    private void updateChatDisplay(Database.scrumdb selected) {
        chatMessagesForEntity.getChildren().clear();
        if (selected != null) {
            for (var m : selected.getLinkedMessages()) {
                String entityInfo = selected instanceof Database.Epic ? "Epic: " + selected.getTitle() :
                        selected instanceof Database.UserStory ? "User Story: " + selected.getTitle() :
                                selected instanceof Database.Task ? "Taak: " + selected.getTitle() :
                                        "Geen Entiteit";

                Label lbl = new Label(
                        String.format("[%s] %s: %s (Gekoppeld aan: %s)",
                                m.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")),
                                m.getSender().getName(),
                                m.getContent(),
                                entityInfo)
                );

                lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: normal;");
                lbl.setWrapText(true);  // <-- belangrijk: zorgt voor afbreken van de tekst
                lbl.setMaxWidth(320);   // <-- pas dit aan naar de breedte van je rechterpaneel minus padding/marges

                chatMessagesForEntity.getChildren().add(0, lbl);
            }
        }
    }

    // **Selecteer een Scrum-element (Epic/User Story/Task) uit de ListView**
    @FXML
    public void handleEntitySelection(MouseEvent event) {
        Database.scrumdb geselecteerd = entityList.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {
            huidige = geselecteerd;  // Geselecteerde Scrum-element
            chatContainer.setVisible(true);  // Toon de chatcontainer voor specifieke Epic/User Story/Task
            updateChatDisplay(geselecteerd);  // Update de chatweergave voor dat item
        }
    }

    // **Maak nieuwe Epic**
    @FXML
    public void maakEpic(ActionEvent e) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setHeaderText("Nieuwe Epic titel:");
        dlg.showAndWait().ifPresent(t -> {
            var ep = new Database.Epic(t);
            epics.add(ep);
            scrumElementen.add(ep);  // Toevoegen aan de juiste lijst van scrumElementen
            updateEntityList();  // Update de ListView
        });
    }

    // **Maak nieuwe User Story**
    @FXML
    public void maakUserStory(ActionEvent e) {
        if (epics.isEmpty()) return;
        Database.Epic epic = epics.get(epics.size() - 1);  // Neem de laatste Epic
        TextInputDialog dlg = new TextInputDialog();
        dlg.setHeaderText("Nieuwe User Story voor Epic \"" + epic.getTitle() + "\":");
        dlg.showAndWait().ifPresent(t -> {
            var us = new Database.UserStory(t);
            epic.addUserStory(us);
            scrumElementen.add(us);  // Voeg de User Story toe aan de scrumElementen lijst
            updateEntityList();  // Update de ListView
        });
    }

    // **Maak nieuwe Taak**
    @FXML
    public void maakTaak(ActionEvent e) {
        for (var elem : scrumElementen) {
            if (elem instanceof Database.UserStory us) {
                TextInputDialog dlg = new TextInputDialog();
                dlg.setHeaderText("Nieuwe Taak voor User Story \"" + us.getTitle() + "\":");
                dlg.showAndWait().ifPresent(t -> {
                    var ta = new Database.Task(t);
                    us.addTask(ta);
                    scrumElementen.add(ta);  // Voeg de Taak toe aan de scrumElementen lijst
                    updateEntityList();  // Update de ListView
                });
                break;
            }
        }
    }

    // **Wijzig het geselecteerde Scrum-element**
    @FXML
    public void wijzigScrumElement(ActionEvent e) {
        if (huidige == null) return;
        TextInputDialog dlg = new TextInputDialog(huidige.getTitle());
        dlg.setHeaderText("Nieuwe titel:");
        dlg.showAndWait().ifPresent(t -> {
            huidige.setTitle(t);
            updateEntityList();  // Update de ListView
        });
    }

    // **Verwijder het geselecteerde Scrum-element**
    @FXML
    public void verwijderScrumElement(ActionEvent e) {
        if (huidige != null) {
            scrumElementen.remove(huidige);
            if (huidige instanceof Database.Epic) {
                epics.remove(huidige);
            }
            huidige = null;
            chatContainer.setVisible(false);  // Verberg de chatcontainer
            updateEntityList();  // Update de ListView
        }
    }

    // **Update de ListView met de Scrum-elementen**
    private void updateEntityList() {
        entityList.setItems(FXCollections.observableArrayList(scrumElementen));  // Weergeven van alle scrum-elementen
    }

    // **Toevoegen van een Scrum-element via de '+' knop in de algemene chat**
    @FXML
    private void showEntitySelectionForMessage(ActionEvent e) {
        String txt = generalChatInput.getText().trim();
        if (!txt.isEmpty()) {
            var msg = new Database.Message(txt, currentUser, LocalDateTime.now(), null); // Maak een bericht zonder gekoppeld element

            // Toon dialoog om een Scrum-element te koppelen aan het bericht
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Koppel Bericht aan Scrum-element");
            alert.setHeaderText("Selecteer een Epic, User Story of Taak om dit bericht aan te koppelen:");

            // Maak een ListView van de scrum-elementen
            ListView<Database.scrumdb> listView = new ListView<>();
            listView.setItems(FXCollections.observableArrayList(scrumElementen));

            alert.getDialogPane().setContent(listView);
            alert.showAndWait().ifPresent(response -> {
                Database.scrumdb selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    msg.setLinkedEntity(selected);  // Koppel het bericht aan het geselecteerde Scrum-element
                    selected.getLinkedMessages().add(msg);  // Voeg het bericht toe aan de geselecteerde entiteit
                    updateGeneralChatDisplay(msg);  // Update de chat met het bericht en het gekoppelde Scrum-element
                }
            });
        }
    }

    @FXML
    private void backToHomeScreen(ActionEvent event) {
        try {
            // Laad het hoofdscherm
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chatapplicatie/hoofdscherm.fxml"));
            Parent root = loader.load();

            // Haal het huidige scherm op
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Maak een nieuw scherm en zet de scène
            Scene scene = new Scene(root);
            currentStage.setScene(scene);

            // Zet de grootte van het hoofdscherm naar 1920x1080
            currentStage.setWidth(1920);
            currentStage.setHeight(1080);

            // Zet de titel en toon het nieuwe scherm
            currentStage.setTitle("Hoofdscherm");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Nieuwe configureEntityListView aanroepen bij opstarten
    @FXML
    public void initialize() {
        configureEntityListView();  // Zorgt dat ListView cellen goed afbreken en stylen
    }

    private void configureEntityListView() {
        entityList.setCellFactory(listView -> new ListCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);  // Zorgt voor automatische regelafbreking
                label.setStyle("-fx-text-fill: #B0B0B0; -fx-padding: 8;");  // Lichtgrijze tekst (thema-gebonden)
            }

            @Override
            protected void updateItem(Database.scrumdb item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item.toString());
                    label.setMaxWidth(entityList.getWidth() - 24); // Houd marge aan voor scrollbar
                    setGraphic(label);
                }
            }
        });
    }
}
