import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ui {

    public boolean handleInput(String input, ArrayList<Task> tasks) throws ChatBotException{
        String markRegex = "^mark \\d+";
        String unmarkRegex = "^unmark \\d+";
        String todoRegex = "^todo (.*)";
        String deadlineRegex = "^deadline (.*) /by (.+)";
        String eventRegex = "^event (.*) /from (.+) /to (.+)$";
        String deleteRegex = "^delete \\d+";

        // Prep for extracting regex
        Pattern todoPattern = Pattern.compile(todoRegex);
        Matcher todoMatcher = todoPattern.matcher(input);

        Pattern deadlinePattern = Pattern.compile(deadlineRegex);
        Matcher deadlineMatcher = deadlinePattern.matcher(input);

        Pattern eventPattern = Pattern.compile(eventRegex);
        Matcher eventMatcher = eventPattern.matcher(input);

        System.out.println("------------------------------------");

        if (input.equals("bye")) { // Exit
            this.endConversation();
            return false;
        } else if (input.equals("list")) { // List all tasks
            this.listTasks(tasks);
        } else if (input.matches(markRegex)) { // Mark as done
            int i = Integer.parseInt(input.split(" ")[1]);
            if (i > tasks.size()) {
                throw new ChatBotException("OOPS!!! Task does not exist.");
            }
            Task t = tasks.get(i - 1);
            t.markAsDone();
            this.showMarkedAsDone(t);
        } else if (input.matches(unmarkRegex)) { // Mark as undone
            int i = Integer.parseInt(input.split(" ")[1]);
            if (i > tasks.size()) {
                throw new ChatBotException("OOPS!!! Task does not exist.");
            }
            Task t = tasks.get(i - 1);
            t.markAsUndone();
           this.showMarkedAsUndone(t);
        } else if (input.matches(deleteRegex)) { // Remove tasks
            int i = Integer.parseInt(input.split(" ")[1]);
            if (i > tasks.size()) {
                throw new ChatBotException("OOPS!!! Task does not exist.");
            }
            Task t = tasks.get(i - 1);
            tasks.remove(i - 1);
            this.showDeleted(t, tasks.size());
        } else if (todoMatcher.matches() || deadlineMatcher.matches() || eventMatcher.matches()) { // Add tasks
            System.out.println("Got it. I've added this task:");
            Task addedTask;
            if (todoMatcher.matches()) {
                // Extract regex
                String description = todoMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! The description of a task cannot be empty.");
                }
                // Create new task
                addedTask = new Todo(description);
            } else if (deadlineMatcher.matches()) {
                // Extract regex
                String description = deadlineMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! The description of a deadline cannot be empty.");
                }
                String by = deadlineMatcher.group(2).trim();
                // Create new task
                addedTask = new Deadline(description, by);
            } else {
                // Extract regex
                String description = eventMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! The description of an event cannot be empty.");
                }
                String from = eventMatcher.group(2).trim();
                String to = eventMatcher.group(3).trim();
                // Create new task
                addedTask = new Event(description, from, to);
            }
            tasks.add(addedTask);
            this.showAddedTask(addedTask, tasks.size());
        } else {
            throw new ChatBotException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
        System.out.println("------------------------------------");
        return true;
    }

    public void endConversation() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("------------------------------------");
    }

    public void listTasks(ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task curr = tasks.get(i);
            System.out.printf("%d.%s\n", i + 1, curr.toString());
        }
    }

    public void showMarkedAsDone(Task t) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(t);
    }

    public void showMarkedAsUndone(Task t) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(t);
    }

    public void showDeleted(Task t, int size) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(t);
        System.out.printf("Now you have %d tasks in the list.\n", size);
    }

    public void showAddedTask(Task t, int size) {
        System.out.println(t);
        System.out.printf("Now you have %d tasks in the list.\n", size);
    }

    public void showLoadingError() {
        System.out.println("Error");
    }
}
