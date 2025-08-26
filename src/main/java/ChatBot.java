import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;


public class ChatBot {

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/tasks.txt");
        try {
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
                    ui.handleInput(input, tasks);
                } catch (ChatBotException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
