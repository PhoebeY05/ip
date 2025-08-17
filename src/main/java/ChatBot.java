import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBot {

    public static void main(String[] args) {
        System.out.println("------------------------------------");
        System.out.println("Hello! I'm ChatBot!");
        System.out.println("What can I do for you?");
        System.out.println("------------------------------------");

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();
        Pattern mark = Pattern.compile("mark \\d+");
        Pattern unmark = Pattern.compile("unmark \\d+");

        while (true) {
            String input = scanner.nextLine();
            Matcher markMatcher = mark.matcher(input);
            Matcher unmarkMatcher = unmark.matcher(input);

            System.out.println("------------------------------------");
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("------------------------------------");
                break;
            } else if (input.equals("list")) {
                for (int i = 0; i < tasks.size(); i++) {
                    Task curr = tasks.get(i);
                    System.out.printf("%d.%s\n", i + 1, curr.toString());
                }
            } else if (markMatcher.matches()) {
                int i = Integer.parseInt(input.split(" ")[1]) - 1;
                Task t = tasks.get(i);
                t.markAsDone();
                System.out.println("Nice! I've marked this task as done: ");
                System.out.println(t);
            } else if (unmarkMatcher.matches()) {
                int i = Integer.parseInt(input.split(" ")[1]) - 1;
                Task t = tasks.get(i);
                t.markAsUndone();
                System.out.println("OK, I've marked this task as not done yet: ");
                System.out.println(t);
            } else {
                Task t = new Task(input);
                tasks.add(t);
                System.out.println("added: " + input);
            }
            System.out.println("------------------------------------");
        }
    }
}
