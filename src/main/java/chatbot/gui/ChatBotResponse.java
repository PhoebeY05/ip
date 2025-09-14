package chatbot.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a Label containing text from the speaker.
 * Supports normal and error messages (e.g., ChatBotException).
 */
public class ChatBotResponse extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a ChatBotResponse with the given text and image.
     *
     * @param text    The message content.
     * @param img     The speaker's display picture.
     * @param isError Whether this message is an error message.
     */
    private ChatBotResponse(String text, Image img, boolean isError) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/ChatBotResponse.fxml"));
            fxmlLoader.setController(this); // This instance acts as controller
            fxmlLoader.setRoot(this);       // Sets this instance as root
            fxmlLoader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        dialog.setText(text);

        // Style the dialog based on whether it's an error
        if (isError) {
            dialog.setStyle(
                    "-fx-background-color: #FFCCCC; " +         // softer pastel red
                            "-fx-text-fill: #990000; " +                // dark red text
                            "-fx-background-radius: 18 18 18 4; " +    // rounded with tail effect
                            "-fx-padding: 12 14 12 14; " +             // top/right/bottom/left padding
                            "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 3, 0.5, 0, 2);"
            );
        } else {
            dialog.setStyle(
                    "-fx-background-color: #E8E8E8; " +        // soft gray
                            "-fx-text-fill: #333333; " +               // dark gray text
                            "-fx-background-radius: 18 18 18 4; " +    // same bubble shape for asymmetry
                            "-fx-padding: 12 14 12 14; " +
                            "-fx-font-size: 14px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0.5, 0, 1);"
            );
        }


        // Clip the display picture to a circle
        Circle clipCircle = new Circle(displayPicture.getFitWidth() / 2, displayPicture.getFitHeight() / 2,
                displayPicture.getFitWidth() / 2);
        displayPicture.setClip(clipCircle);
        displayPicture.setImage(img);
    }

    /**
     * Creates a normal chatbot response.
     *
     * @param text The message content.
     * @param img  The speaker image.
     * @return ChatBotResponse instance.
     */
    public static ChatBotResponse getChatBotResponse(String text, Image img) {
        return new ChatBotResponse(text, img, false);
    }

    /**
     * Creates an error message chatbot response.
     *
     * @param text The error message content.
     * @param img  The speaker image.
     * @return ChatBotResponse instance styled as an error.
     */
    public static ChatBotResponse getErrorResponse(String text, Image img) {
        return new ChatBotResponse(text, img, true);
    }
}
