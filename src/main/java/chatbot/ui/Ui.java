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
     * @param task    The task that was deleted.
     * @param totalTasks The updated size of the task list.
     */
    public String showDeleted(Task task, int totalTasks) {
        String message = "Noted. I've removed this task:\n";
        message += task;
        message += String.format("\nNow you have %d task(s) in the list.\n", totalTasks);
        return message;
    }
    /**
     * Displays a confirmation that a task has been added and
     * shows the updated number of tasks in the list.
     *
     * @param task    The task that was added.
     * @param totalTasks The updated size of the task list.
     */
    public String showAddedTask(Task task, int totalTasks) {
        String message = "Noted. I've added this task: \n";
        message += task;
        message += String.format("\nNow you have %d task(s) in the list.\n", totalTasks);
        return message;
    }

    public String showFindResult(TaskList tasks) {
        String message = "Here are the matching tasks in your list:\n";
        return message + tasks;
    }

    /**
     * Displays an error message if there was an error loading saved tasks.
     */
    public String showLoadingError(Exception e) {
        return "LOADING ERROR\n" + e.getMessage();
    }

    /**
     * Displays a welcome message on launch.
     */
    public String showWelcomeMessage() {
        String message = "Hello! I'm ChatBot!\n";
        message += "What can I do for you?";
        return message;
    }
}
