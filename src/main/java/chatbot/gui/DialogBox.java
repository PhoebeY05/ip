package chatbot.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a Label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a DialogBox with the given text and image.
     *
     * @param text The message content.
     * @param img  The speaker's display picture.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this); // This DialogBox instance acts as its controller
            fxmlLoader.setRoot(this);       // Sets this instance as the root layout
            fxmlLoader.load();              // Loads FXML into this object
        } catch (IOException e) {
            // Printing stack trace is usually not ideal; consider logging instead
            e.printStackTrace();
        }

        dialog.setText(text);          // Sets the dialog text
        Circle clipCircle = new Circle(displayPicture.getFitWidth() / 2, displayPicture.getFitHeight() / 2, displayPicture.getFitWidth() / 2);
        displayPicture.setClip(clipCircle);
        displayPicture.setImage(img);  // Sets the speaker image
    }


    /**
     * Creates a DialogBox for user messages (image on the right).
     *
     * @param text The user message.
     * @param img  The user image.
     * @return A DialogBox instance representing the user dialog.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

}
