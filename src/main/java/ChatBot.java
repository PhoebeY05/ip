import java.util.ArrayList;
import java.util.Scanner;


public class ChatBot {

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/tasks.txt");
        System.out.println("------------------------------------");
        System.out.println("Hello! I'm ChatBot!");
        System.out.println("What can I do for you?");
        System.out.println("------------------------------------");

        Scanner scanner = new Scanner(System.in);
        TaskList tasks = new TaskList();

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
}
