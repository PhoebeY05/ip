package chatbot;

import java.util.Scanner;

import chatbot.command.Parser;
import chatbot.exception.ChatBotException;
import chatbot.storage.Storage;
import chatbot.task.TaskList;
import chatbot.ui.Ui;

/**
 * The main class for the ChatBot application.
 * Handles initialization of storage, task list, and UI,
 * and manages the main interaction loop with the user.
 */
public class ChatBot {

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Constructs a ChatBot instance with the specified storage file path.
     * Loads existing tasks from storage, or initializes an empty task list if loading fails.
     *
     * @param filePath Path to the file where tasks are stored.
     */
    public ChatBot(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        try {
            tasks = new TaskList(storage.load());
        } catch (ChatBotException e) {
            ui.showLoadingError();
            System.out.println(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main chatbot loop, interacting with the user through the console.
     * Continuously reads user input, parses commands, and updates tasks until
     * the {@code BYE} command is given.
     * <p>
     * The task list is saved to storage after each user input.
     */
    public void run() {
        System.out.println("------------------------------------");
        System.out.println("Hello! I'm ChatBot!");
        System.out.println("What can I do for you?");
        System.out.println("------------------------------------");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            storage.saveToStorage(tasks);

            String input = scanner.nextLine();
            Parser parser = new Parser(input);

            try {
                if (!ui.handleInput(parser, tasks)) {
                    break;
                }
            } catch (ChatBotException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Entry point for the ChatBot application.
     * Creates a new ChatBot instance and starts the interaction loop.
     *
     * @param args Command-line arguments (ignored).
     */
    public static void main(String[] args) {
        new ChatBot("data/tasks.txt").run();
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        return "ChatBot heard: " + input;
    }
}
