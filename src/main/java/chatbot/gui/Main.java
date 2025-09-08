package chatbot.gui;

import java.io.IOException;

import chatbot.ChatBot;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for ChatBot using FXML.
 */
public class Main extends Application {

    private final ChatBot chatbot = new ChatBot("data/tasks.txt"); // ChatBot instance

    @Override
    public void start(Stage stage) {
        try {
            // Load FXML layout for main window
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();

            // Create a scene with the loaded layout and set it to the stage
            Scene scene = new Scene(mainLayout);
            stage.setScene(scene);

            // Inject ChatBot instance into the controller
            fxmlLoader.<MainWindow>getController().setChatBot(chatbot);

            stage.show(); // Display the stage
        } catch (IOException e) {
            // Handle FXML loading errors
            e.printStackTrace(); // Consider proper logging in production
        }
    }
}
