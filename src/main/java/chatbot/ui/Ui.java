package chatbot.ui;

import chatbot.command.Parser;
import chatbot.task.Task;
import chatbot.task.TaskList;

/**
 * Handles all interactions with the user by printing messages to the console.
 * The {@code Ui} class is responsible for displaying chatbot responses
 * (e.g. adding, marking, deleting tasks) and handling user input via a {@link Parser}.
 */
public class Ui {

    /**
     * Displays a farewell message and ends the chatbot conversation.
     */
    public String endConversation() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks The current task list.
     */
    public String listTasks(TaskList tasks) {
        return tasks.toString();
    }

    /**
     * Displays a confirmation that a task has been marked as done.
     *
     * @param task The task that was marked as done.
     */
    public String showMarkedAsDone(Task task) {
        // Include task string after confirmation
        return "Nice! I've marked this task as done:\n" + task;
    }

    /**
     * Displays a confirmation that a task has been marked as not done.
     *
     * @param task The task that was marked as undone.
     */
    public String showMarkedAsUndone(Task task) {
        return "OK, I've marked this task as not done yet:\n" + task;
    }

    /**
     * Displays a confirmation that a task has been deleted and
     * shows the updated number of tasks remaining.
     *
     * @param task       The task that was deleted.
     * @param totalTasks The updated size of the task list.
     */
    public String showDeleted(Task task, int totalTasks) {
        // Build message using StringBuilder for efficiency
        StringBuilder message = new StringBuilder();
        message.append("Noted. I've removed this task:\n");
        message.append(task);
        message.append(String.format("\nNow you have %d task(s) in the list.\n", totalTasks));
        return message.toString();
    }

    /**
     * Displays a confirmation that a task has been added and
     * shows the updated number of tasks in the list.
     *
     * @param task       The task that was added.
     * @param totalTasks The updated size of the task list.
     */
    public String showAddedTask(Task task, int totalTasks) {
        StringBuilder message = new StringBuilder();
        message.append("Noted. I've added this task:\n");
        message.append(task);
        message.append(String.format("\nNow you have %d task(s) in the list.\n", totalTasks));
        return message.toString();
    }

    /**
     * Displays tasks that match a search query.
     *
     * @param tasks TaskList containing matching tasks.
     */
    public String showFindResult(TaskList tasks) {
        // Prefix with explanation before listing tasks
        StringBuilder message = new StringBuilder();
        message.append("Here are the matching tasks in your list:\n");
        message.append(tasks);
        return message.toString();
    }

    /**
     * Displays an error message if there was an error loading saved tasks.
     *
     * @param e The exception that occurred.
     */
    public String showLoadingError(Exception e) {
        return "LOADING ERROR\n" + e.getMessage();
    }

    /**
     * Displays a welcome message on launch.
     */
    public String showWelcomeMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Hello! I'm ChatBot!\n");
        message.append("What can I do for you?");
        return message.toString();
    }

    // Used ChatGPT to add UI functions to return message instead of returning string directly

    /**
     * Formats and returns a message displaying the available free time range.
     *
     * @param start Formatted start datetime string.
     * @param end   Formatted end datetime string.
     * @return Message showing the free time slot.
     */
    public String showFreeTimeRange(String start, String end) {
        return "Your next available free time slot is from " + start + " to " + end + ".";
    }

}
