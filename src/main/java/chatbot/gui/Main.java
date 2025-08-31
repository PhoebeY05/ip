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

    private ChatBot chatbot = new ChatBot("data/tasks.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setChatBot(chatbot);  // inject the ChatBot instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
