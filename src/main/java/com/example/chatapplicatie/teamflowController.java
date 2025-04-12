package com.example.chatapplicatie;

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
import java.util.HashMap;
import java.util.Map;

public class teamflowController {

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

    private Map<String, VBox> cardChats = new HashMap<>();
    private String currentChatCard = "";

    @FXML
    private void switchToUI(ActionEvent event) throws IOException {
        System.out.println("Knop gedrukt, switchen naar UI.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chatapplicatie/UI.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("User Story1 Chat");
        stage.show();
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
        cardContainer.setStyle("-fx-background-color: #1f2937; -fx-padding: 8; -fx-background-radius: 4; -fx-margin: 0 0 5 0;");
        cardContainer.setMaxWidth(200);

        // Maak de card dragbaar
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
            cardChats.remove(cardName);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        cardContainer.getChildren().addAll(cardLabel, spacer, detailBtn, chatBtn, deleteBtn);

        // Voeg de card toe boven de "Add Card"-knop
        int addCardIndex = parentVBox.getChildren().indexOf(inputBox);
        parentVBox.getChildren().add(addCardIndex, cardContainer);
        parentVBox.getChildren().remove(inputBox);

        // Voeg wat margin toe onder de card voor ruimte
        VBox.setMargin(cardContainer, new Insets(0, 0, 5, 0));

        if (!cardChats.containsKey(cardName)) {
            cardChats.put(cardName, new VBox());
        }
    }

    private void setupDragAndDrop() {
        // Voeg drag-and-drop handlers toe aan alle lijsten
        setupListDragAndDrop(sprintBacklog);
        setupListDragAndDrop(sprint1);
        setupListDragAndDrop(toDo);
        setupListDragAndDrop(inProgress);
        setupListDragAndDrop(sprintReview);
        setupListDragAndDrop(done);
    }

    private void setupListDragAndDrop(VBox list) {
        // Laat toe om items te ontvangen
        list.setOnDragOver(event -> {
            if (event.getGestureSource() != list && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Verwerk het neerzetten van een item
        list.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String cardName = db.getString();

                // Zoek de card in alle lijsten
                HBox cardToMove = findCardByName(cardName);
                if (cardToMove != null) {
                    // Verwijder de card van zijn huidige positie
                    removeCardFromAnyList(cardToMove);

                    // Voeg de card toe aan de nieuwe lijst
                    int addIndex = list.getChildren().size() - 3; // Voeg toe boven de "Add Card" knop
                    if (addIndex < 0) addIndex = 0;
                    list.getChildren().add(addIndex, cardToMove);

                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private HBox findCardByName(String cardName) {
        // Zoek in alle lijsten naar de card met de gegeven naam
        for (VBox list : new VBox[]{sprintBacklog, sprint1, toDo, inProgress, sprintReview, done}) {
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
        // Verwijder de card van welke lijst dan ook
        for (VBox list : new VBox[]{sprintBacklog, sprint1, toDo, inProgress, sprintReview, done}) {
            if (list.getChildren().remove(cardToRemove)) {
                break;
            }
        }
    }

    private void showCardDetails(String cardName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Card Details");
        alert.setHeaderText("Details voor: " + cardName);
        alert.setContentText("Hier komen de details van de card.\nJe kunt dit uitbreiden met meer informatie.");
        alert.showAndWait();
    }

    private void openChat(String cardName) {
        currentChatCard = cardName;
        chatContainer.setVisible(true);
        chatMessages.getChildren().clear();

        // Laad bestaande chatberichten als die er zijn
        if (cardChats.containsKey(cardName)) {
            chatMessages.getChildren().addAll(cardChats.get(cardName).getChildren());
        }
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        String message = chatInput.getText();
        if (!message.isEmpty() && !currentChatCard.isEmpty()) {
            Label messageLabel = new Label(currentChatCard + ": " + message);
            messageLabel.setStyle("-fx-text-fill: white; -fx-padding: 5; -fx-background-color: #3b82f6; -fx-background-radius: 5;");

            // Voeg bericht toe aan huidige chat en aan de card's chatgeschiedenis
            chatMessages.getChildren().add(messageLabel);
            cardChats.get(currentChatCard).getChildren().add(new Label(messageLabel.getText()));
            chatInput.clear();
        }
    }

    @FXML
    private void closeChat(ActionEvent event) {
        chatContainer.setVisible(false);
    }

    @FXML
    public void initialize() {
        setupDragAndDrop();
    }
}