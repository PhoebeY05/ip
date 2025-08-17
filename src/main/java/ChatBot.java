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

        // Regex
        String markRegex ="^mark \\d+";
        String unmarkRegex = "^unmark \\d+";
        String todoRegex = "^todo (.*)";
        String deadlineRegex = "^deadline (.*) /by (\\w+)";
        String eventRegex = "^event (.*) /from (.+) /to (.+)$";
        String deleteRegex = "^delete \\d+";
        try {
            while (true) {
                String input = scanner.nextLine();

                // Prep for extracting regex
                Pattern todoPattern = Pattern.compile(todoRegex);
                Matcher todoMatcher = todoPattern.matcher(input);

                Pattern deadlinePattern = Pattern.compile(deadlineRegex);
                Matcher deadlineMatcher = deadlinePattern.matcher(input);

                Pattern eventPattern = Pattern.compile(eventRegex);
                Matcher eventMatcher = eventPattern.matcher(input);

                System.out.println("------------------------------------");
                if (input.equals("bye")) { // Exit
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println("------------------------------------");
                    break;
                } else if (input.equals("list")) { // List all tasks
                    for (int i = 0; i < tasks.size(); i++) {
                        Task curr = tasks.get(i);
                        System.out.printf("%d.%s\n", i + 1, curr.toString());
                    }
                } else if (input.matches(markRegex)) { // Mark as done
                    int i = Integer.parseInt(input.split(" ")[1]);
                    if (i > tasks.size()) {
                        throw new ChatBotException("OOPS!!! Task does not exist.");
                    }
                    Task t = tasks.get(i - 1);
                    t.markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println(t);
                } else if (input.matches(unmarkRegex)) { // Mark as undone
                    int i = Integer.parseInt(input.split(" ")[1]);
                    if (i > tasks.size()) {
                        throw new ChatBotException("OOPS!!! Task does not exist.");
                    }
                    Task t = tasks.get(i - 1);
                    t.markAsUndone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println(t);
                } else if (input.matches(deleteRegex)) { // Remove tasks
                    int i = Integer.parseInt(input.split(" ")[1]);
                    if (i > tasks.size()) {
                       throw new ChatBotException("OOPS!!! Task does not exist.");
                    }
                    Task t = tasks.get(i - 1);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println(t);
                    tasks.remove(i - 1);
                    System.out.printf("Now you have %d tasks in the list.\n", tasks.size());
                } else { // Add tasks
                    System.out.println("Got it. I've added this task:");
                    if (todoMatcher.matches()) {
                        // Extract regex
                        String description = todoMatcher.group(1).trim();
                        if (description.isEmpty()) {
                            throw new ChatBotException("OOPS!!! The description of a task cannot be empty.");
                        }
                        // Create new task
                        Todo t = new Todo(description);
                        tasks.add(t);
                        System.out.println(t);
                    } else if (deadlineMatcher.matches()) {
                        // Extract regex
                        String description = deadlineMatcher.group(1).trim();
                        if (description.isEmpty()) {
                            throw new ChatBotException("OOPS!!! The description of a deadline cannot be empty.");
                        }
                        String by = deadlineMatcher.group(2).trim();
                        // Create new task
                        Deadline d = new Deadline(description, by);
                        tasks.add(d);
                        System.out.println(d);
                    } else if (eventMatcher.matches()) {
                        // Extract regex
                        String description = eventMatcher.group(1).trim();
                        if (description.isEmpty()) {
                            throw new ChatBotException("OOPS!!! The description of an event cannot be empty.");
                        }
                        String from = eventMatcher.group(2).trim();
                        String to = eventMatcher.group(3).trim();
                        // Create new task
                        Event e = new Event(description, from, to);
                        tasks.add(e);
                        System.out.println(e);
                    } else {
                        throw new ChatBotException("OOPS!!! I'm sorry, but I don't know what that means :-(");
                    }
                    System.out.printf("Now you have %d tasks in the list.\n", tasks.size());
                }
                System.out.println("------------------------------------");
            }
        } catch (ChatBotException e) {
            System.out.println(e.getMessage());
        }
    }
}
