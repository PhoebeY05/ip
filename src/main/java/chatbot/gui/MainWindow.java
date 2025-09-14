package chatbot.gui;

import chatbot.ChatBot;
import chatbot.exception.ChatBotException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private ChatBot chatbot;

    // Images for the user and the chatbot
    private final Image userImage = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/DaUser.png")));
    private final Image chatbotImage = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/DaDuke.png")));

    /**
     * Initializes the GUI. Binds the scroll pane to the height of the dialog container
     * so that new messages are visible automatically.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        ChatBotResponse welcomeMessage = ChatBotResponse.getChatBotResponse("Hello! I am your chatbot. How can I assist you today?", chatbotImage);
        dialogContainer.getChildren().add(welcomeMessage);
    }

    /**
     * Injects the ChatBot instance.
     *
     * @param chatbot The ChatBot instance to use
     */
    public void setChatBot(ChatBot chatbot) {
        this.chatbot = chatbot;
    }

    /**
     * Handles user input: creates dialog boxes for the user input and chatbot response,
     * adds them to the dialog container, and clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String inputText = userInput.getText();                  // Get user input

        // Add user dialog to the container
        dialogContainer.getChildren().add(
                DialogBox.getUserDialog(inputText, userImage)
        );

        try {
            String response = chatbot.getResponse(inputText); // may throw ChatBotException
            dialogContainer.getChildren().add(
                    ChatBotResponse.getChatBotResponse(response, chatbotImage)
            );
        } catch (ChatBotException e) {
            dialogContainer.getChildren().add(
                    ChatBotResponse.getErrorResponse("Error: " + e.getMessage(), chatbotImage)
            );
        }
        userInput.clear(); // Clear input field for next message
    }
}
