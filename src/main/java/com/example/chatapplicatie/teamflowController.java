package com.example.chatapplicatie;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class teamflowController {

    // UI elementen
    @FXML private Button btnUserStory1;
    @FXML private VBox chatContainer;
    @FXML private VBox chatMessages;
    @FXML private TextField chatInput;
    @FXML private Button closeChatBtn;
    @FXML private VBox sprintBacklog;
    @FXML private VBox sprint1;
    @FXML private VBox toDo;
    @FXML private VBox inProgress;
    @FXML private VBox sprintReview;
    @FXML private VBox done;

    // Data structuren
    private Database.User currentUser;
    private Map<String, Database.scrumdb> entities = new HashMap<>();
    private Database.scrumdb currentlySelectedEntity;

    @FXML
    public void initialize() {
        currentUser = new Database.User(System.getProperty("user.name", "User"));
        setupDragAndDrop();
    }
    @FXML private VBox taskChatContainer;
    @FXML private ListView<String> taskChatMessages;
    @FXML private TextField taskChatInput;

    private void setupDragAndDrop() {
        List<VBox> columns = Arrays.asList(sprintBacklog, sprint1, toDo, inProgress, sprintReview, done);

        for (VBox column : columns) {
            column.setOnDragOver(event -> {
                if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            column.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    String cardName = db.getString();
                    HBox cardToMove = findCardByName(cardName);

                    if (cardToMove != null) {
                        removeCardFromAnyList(cardToMove);
                        int addIndex = column.getChildren().size() - 1;
                        column.getChildren().add(addIndex, cardToMove);
                        updateTaskStatus(cardName, column.getId());
                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }
    }

    private void updateTaskStatus(String cardName, String columnId) {
        Database.scrumdb entity = entities.get(cardName);
        if (entity instanceof Database.Task) {
            Database.Task task = (Database.Task) entity;
            switch (columnId) {
                case "toDo":
                    task.setStatus(Database.TaskStatus.TODO);
                    break;
                case "inProgress":
                    task.setStatus(Database.TaskStatus.IN_PROGRESS);
                    break;
                case "sprintReview":
                    task.setStatus(Database.TaskStatus.REVIEW);
                    break;
                case "done":
                    task.setStatus(Database.TaskStatus.DONE);
                    break;
            }
        }
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        String messageText = chatInput.getText();
        if (!messageText.isEmpty() && currentlySelectedEntity != null) {
            Database.Message message = new Database.Message(
                    messageText,
                    currentUser,
                    LocalDateTime.now(),
                    currentlySelectedEntity
            );
            currentlySelectedEntity.getLinkedMessages().add(message);
            updateChatDisplay();
            chatInput.clear();
        }
    }

    private void updateChatDisplay() {
        chatMessages.getChildren().clear();
        if (currentlySelectedEntity != null) {
            for (Database.Message msg : currentlySelectedEntity.getLinkedMessages()) {
                HBox messageBox = new HBox();
                messageBox.setStyle("-fx-padding: 5;");
                Label messageLabel = new Label(
                        String.format("[%s] %s: %s",
                                msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")),
                                msg.getSender().getName(),
                                msg.getContent())
                );
                messageLabel.setStyle("-fx-text-fill: white;");
                messageBox.getChildren().add(messageLabel);
                chatMessages.getChildren().add(messageBox);
            }
        }
    }

    @FXML
    private void closeChat(ActionEvent event) {
        chatContainer.setVisible(false);
    }

    private void openChat(String cardName) {
        currentlySelectedEntity = entities.get(cardName);
        if (currentlySelectedEntity == null) {
            currentlySelectedEntity = new Database.Task(cardName);
            entities.put(cardName, currentlySelectedEntity);
        }
        chatContainer.setVisible(true);
        updateChatDisplay();
    }

    private HBox findCardByName(String cardName) {
        for (VBox list : Arrays.asList(sprintBacklog, sprint1, toDo, inProgress, sprintReview, done)) {
            for (Node node : list.getChildren()) {
                if (node instanceof HBox) {
                    HBox cardContainer = (HBox) node;
                    for (Node child : cardContainer.getChildren()) {
                        if (child instanceof Label && ((Label) child).getText().equals(cardName)) {
                            return cardContainer;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void removeCardFromAnyList(HBox cardToRemove) {
        for (VBox list : Arrays.asList(sprintBacklog, sprint1, toDo, inProgress, sprintReview, done)) {
            if (list.getChildren().remove(cardToRemove)) {
                break;
            }
        }
    }

    @FXML
    public void handleAddCard(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        HBox container = (HBox) clickedButton.getParent();
        VBox parentVBox = (VBox) container.getParent();

        HBox inputBox = new HBox(10);
        inputBox.setStyle("-fx-padding: 5;");

        TextField cardNameField = new TextField();
        cardNameField.setPromptText("Card name...");
        cardNameField.setStyle("-fx-background-color: #1f2937; -fx-text-fill: white;");
        cardNameField.setPrefWidth(120);

        Button confirmButton = new Button("Add");
        confirmButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");

        Button cancelButton = new Button("✕");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        inputBox.getChildren().addAll(cardNameField, confirmButton, cancelButton);
        parentVBox.getChildren().add(parentVBox.getChildren().indexOf(container), inputBox);

        confirmButton.setOnAction(e -> {
            String cardName = cardNameField.getText();
            if (!cardName.isEmpty()) {
                createCard(parentVBox, inputBox, cardName);
            }
        });

        cancelButton.setOnAction(e -> parentVBox.getChildren().remove(inputBox));
    }

    private void createCard(VBox parentVBox, HBox inputBox, String cardName) {
        HBox cardContainer = new HBox(5);
        cardContainer.setStyle("-fx-background-color: #1f2937; -fx-padding: 8; -fx-background-radius: 4;");
        cardContainer.setMaxWidth(200);

        cardContainer.setOnDragDetected(event -> {
            Dragboard db = cardContainer.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(cardName);
            db.setContent(content);
            event.consume();
        });

        Label cardLabel = new Label(cardName);
        cardLabel.setStyle("-fx-text-fill: white;");
        cardLabel.setMaxWidth(100);

        Button detailBtn = new Button("🔍");
        detailBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        detailBtn.setOnAction(e -> showCardDetails(cardName));

        Button chatBtn = new Button("💬");
        chatBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        chatBtn.setOnAction(e -> openChat(cardName));

        Button deleteBtn = new Button("✕");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            parentVBox.getChildren().remove(cardContainer);
            entities.remove(cardName);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        cardContainer.getChildren().addAll(cardLabel, spacer, detailBtn, chatBtn, deleteBtn);
        parentVBox.getChildren().add(parentVBox.getChildren().indexOf(inputBox), cardContainer);
        parentVBox.getChildren().remove(inputBox);
        VBox.setMargin(cardContainer, new Insets(0, 0, 5, 0));

        if (!entities.containsKey(cardName)) {
            entities.put(cardName, new Database.Task(cardName));
        }
    }

    private void showCardDetails(String cardName) {
        Database.scrumdb entity = entities.get(cardName);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Card Details");
        alert.setHeaderText("Details voor: " + cardName);

        if (entity != null) {
            StringBuilder content = new StringBuilder();

            if (entity instanceof Database.Task) {
                Database.Task task = (Database.Task) entity;
                content.append("Type: Taak\n")
                        .append("Status: ").append(task.getStatus()).append("\n")
                        .append("Toegewezen aan: ").append(task.getAssignedTo() != null ? task.getAssignedTo().getName() : "Niemand").append("\n")
                        .append("Geschatte uren: ").append(task.getEstimatedHours()).append("\n");
            } else if (entity instanceof Database.UserStory) {
                Database.UserStory userStory = (Database.UserStory) entity;
                content.append("Type: User Story\n")
                        .append("Acceptatiecriteria: ").append(userStory.getAcceptanceCriteria()).append("\n");
            } else if (entity instanceof Database.Epic) {
                content.append("Type: Epic\n");
            }

            content.append("\nAantal chatberichten: ").append(entity.getLinkedMessages().size());
            alert.setContentText(content.toString());
        } else {
            alert.setContentText("Geen extra informatie beschikbaar");
        }

        alert.showAndWait();
    }
    @FXML
    private void sendTaskMessage(ActionEvent event) {
        // Gebruik dezelfde implementatie als sendMessage maar voor taskChatInput
        String messageText = taskChatInput.getText();
        if (!messageText.isEmpty() && currentlySelectedEntity != null) {
            Database.Message message = new Database.Message(
                    messageText,
                    currentUser,
                    LocalDateTime.now(),
                    currentlySelectedEntity
            );
            currentlySelectedEntity.getLinkedMessages().add(message);
            updateChatDisplay();
            taskChatInput.clear();
        }
    }

    @FXML
    private void showLinkDialog(ActionEvent event) {
        // Implementatie gebaseerd op eerder besproken LinkDialog
        LinkDialog linkDialog = new LinkDialog();
        linkDialog.setEntities(new ArrayList<>(entities.values()));
        linkDialog.setCurrentlySelectedMessage(getLastMessage());
        linkDialog.setUpdateChatDisplayCallback(this::updateChatDisplay);
        linkDialog.showLinkDialog();
    }

    private Database.Message getLastMessage() {
        if (currentlySelectedEntity != null && !currentlySelectedEntity.getLinkedMessages().isEmpty()) {
            return currentlySelectedEntity.getLinkedMessages().get(
                    currentlySelectedEntity.getLinkedMessages().size() - 1
            );
        }
        return null;
    }
}