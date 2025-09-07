package chatbot.ui;

import java.util.ArrayList;
import java.util.regex.Pattern;

import chatbot.command.CommandType;
import chatbot.command.Parser;
import chatbot.exception.ChatBotException;
import chatbot.task.Deadline;
import chatbot.task.Event;
import chatbot.task.Task;
import chatbot.task.TaskList;
import chatbot.task.Todo;

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
    public String handleInput(Parser parser, TaskList tasks) throws ChatBotException {
        CommandType commandType = parser.getCommandType();
        Task addedTask = null;
        ArrayList<String> args = parser.getArguments();

        switch (commandType) {
        case BYE:
            return this.endConversation();

        case LIST:
            return this.listTasks(tasks);

        case MARK:
            Task taskToMark = parser.getTask(tasks);
            taskToMark.markAsDone();
            return this.showMarkedAsDone(taskToMark);

        case UNMARK:
            Task taskToUnmark = parser.getTask(tasks);
            taskToUnmark.markAsUndone();
            return this.showMarkedAsUndone(taskToUnmark);

        case DELETE:
            Task taskToDelete = parser.getTask(tasks);
            tasks.deleteTask(taskToDelete);
            return this.showDeleted(taskToDelete, tasks.getTotalTasks());

        case TODO:
            addedTask = new Todo(args.get(0));
            break;

        case DEADLINE:
            addedTask = new Deadline(args.get(0), args.get(1));
            break;

        case EVENT:
            addedTask = new Event(args.get(0), args.get(1), args.get(2));
            break;

        case FIND:
            String regex = "\\b" + args.get(0) + "\\b";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            TaskList filteredTaskList = tasks.filter(task -> pattern.matcher(task.toString()).find());
            return this.showFindResult(filteredTaskList);
        default:
            throw new ChatBotException(
                    "OOPS!!! I'm sorry, but I don't know what that means :-("
            );
        }

        if (addedTask != null) {
            tasks.addTask(addedTask);
            return this.showAddedTask(addedTask, tasks.getTotalTasks());
        }
        return "";
    }

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
