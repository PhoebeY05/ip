import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;


public class ChatBot {

    private static void saveTasks(ArrayList<Task> tasks, File f) throws Exception {
        try (FileWriter fw = new FileWriter(f, false)) { // false = overwrite
            for (Task t : tasks) {
                fw.write(t + System.lineSeparator());
            }
        }
    }

    public static void main(String[] args) {
        Ui ui = new Ui();
        try {
            System.out.println("------------------------------------");
            System.out.println("Hello! I'm ChatBot!");
            System.out.println("What can I do for you?");
            System.out.println("------------------------------------");

            Scanner scanner = new Scanner(System.in);
            ArrayList<Task> tasks = new ArrayList<>();

            File folder = new File("data");
            if (!folder.exists()) {
                folder.mkdir();  // make directory
            }

            File f = new File(folder, "ChatBot.txt");
            if (!f.exists()) {
                f.createNewFile();
            }


            // Regex
            String markRegex = "^mark \\d+";
            String unmarkRegex = "^unmark \\d+";
            String todoRegex = "^todo (.*)";
            String deadlineRegex = "^deadline (.*) /by (.+)";
            String eventRegex = "^event (.*) /from (.+) /to (.+)$";
            String deleteRegex = "^delete \\d+";
            try {
                while (true) {
                    saveTasks(tasks, f);

                    String input = scanner.nextLine();
                    ui.handleInput(input, tasks);

                }
            } catch (ChatBotException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
