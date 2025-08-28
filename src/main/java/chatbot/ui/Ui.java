package chatbot.ui;

import chatbot.exception.ChatBotException;
import chatbot.task.*;
import chatbot.command.CommandType;
import chatbot.command.Parser;

import java.util.ArrayList;

/**
 * Handles all interactions with the user by printing messages to the console.
 * The {@code Ui} class is responsible for displaying chatbot responses
 * (e.g. adding, marking, deleting tasks) and handling user input via a {@link Parser}.
 */
public class Ui {

    /**
     * Processes user input by interpreting the parsed command
     * and updating the task list accordingly.
     * Terminates the main {@code while(true)} loop if the {@code BYE} command is given.
     *
     * @param parser The parser containing the parsed command and its arguments.
     * @param tasks  The current task list.
     * @return {@code false} if the command is {@link CommandType#BYE}, otherwise {@code true}.
     * @throws ChatBotException If the command is unrecognized or the arguments are invalid.
     */
    public boolean handleInput(Parser parser, TaskList tasks) throws ChatBotException {

        System.out.println("------------------------------------");
        CommandType commandType = parser.getCommandType();
        Task addedTask = null;
        ArrayList<String> args = parser.getArguments();

        switch (commandType) {
            case BYE:
                this.endConversation();
                return false;
            case LIST:
                this.listTasks(tasks);
                break;
            case MARK:
                Task tDone = parser.getTask(tasks);
                tDone.markAsDone();
                this.showMarkedAsDone(tDone);
                break;
            case UNMARK:
                Task tUndone = parser.getTask(tasks);
                tUndone.markAsUndone();
                this.showMarkedAsUndone(tUndone);
                break;
            case DELETE:
                Task tDeleted = parser.getTask(tasks);
                tasks.deleteTask(tDeleted);
                this.showDeleted(tDeleted, tasks.getTotalTasks());
                break;
            case TODO:
                System.out.println("Got it. I've added this task:");
                addedTask = new Todo(args.get(0));
                break;
            case DEADLINE:
                System.out.println("Got it. I've added this task:");
                addedTask = new Deadline(args.get(0), args.get(1));
                break;
            case EVENT:
                System.out.println("Got it. I've added this task:");
                addedTask = new Event(args.get(0), args.get(1), args.get(2));
                break;
            default:
                throw new ChatBotException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }

        if (addedTask != null) {
            tasks.addTask(addedTask);
            this.showAddedTask(addedTask, tasks.getTotalTasks());
        }

        System.out.println("------------------------------------");
        return true;
    }

    /**
     * Displays a farewell message and ends the chatbot conversation.
     */
    public void endConversation() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("------------------------------------");
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks The current task list.
     */
    public void listTasks(TaskList tasks) {
        System.out.println(tasks);
    }

    /**
     * Displays a confirmation that a task has been marked as done.
     *
     * @param t The task that was marked as done.
     */
    public void showMarkedAsDone(Task t) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(t);
    }

    /**
     * Displays a confirmation that a task has been marked as not done.
     *
     * @param t The task that was marked as undone.
     */
    public void showMarkedAsUndone(Task t) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(t);
    }

    /**
     * Displays a confirmation that a task has been deleted and
     * shows the updated number of tasks remaining.
     *
     * @param t    The task that was deleted.
     * @param size The updated size of the task list.
     */
    public void showDeleted(Task t, int size) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(t);
        System.out.printf("Now you have %d task(s) in the list.\n", size);
    }

    /**
     * Displays a confirmation that a task has been added and
     * shows the updated number of tasks in the list.
     *
     * @param t    The task that was added.
     * @param size The updated size of the task list.
     */
    public void showAddedTask(Task t, int size) {
        System.out.println(t);
        System.out.printf("Now you have %d task(s) in the list.\n", size);
    }

    /**
     * Displays an error message if there was an error loading saved tasks.
     */
    public void showLoadingError() {
        System.out.println("LOADING ERROR");
    }
}
