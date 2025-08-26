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
        ArrayList<Task> tasks = new ArrayList<>();


        while (true) {
            storage.saveToStorage(tasks);

            String input = scanner.nextLine();
            try {
                if (!ui.handleInput(input, tasks)) {
                    break;
                }
            } catch (ChatBotException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
