package chatbot;

import chatbot.exception.ChatBotException;
import chatbot.storage.Storage;
import chatbot.task.TaskList;
import chatbot.ui.Ui;
import chatbot.command.Parser;

import java.util.Scanner;

public class ChatBot {

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

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

    public static void main(String[] args) {
        new ChatBot("data/tasks.txt").run();
    }
}