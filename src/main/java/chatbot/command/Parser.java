package chatbot.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.exception.ChatBotException;
import chatbot.task.*;
import chatbot.ui.Ui;


/**
 * Parses user input into a recognized chatbot command.
 * Supports commands such as adding tasks (todo, deadline, event),
 * modifying tasks (mark, unmark, delete), listing tasks, and exiting.
 */
public class Parser {

    private final String input;
    private final CommandType command;

    private final Matcher todoMatcher;
    private final Matcher deadlineMatcher;
    private final Matcher eventMatcher;
    private final Matcher findMatcher;

    /**
     * Constructs a Parser object and determines the command type
     * based on the given user input. Also prepares regex matchers
     * for extracting task details in other methods.
     *
     * @param input Raw user input string.
     */
    public Parser(String input) {
        this.input = input;

        String markRegex = "^mark \\d+";
        String unmarkRegex = "^unmark \\d+";
        String todoRegex = "^todo (.*)";
        String deadlineRegex = "^deadline (.*) /by (.+)";
        String eventRegex = "^event (.*) /from (.+) /to (.+)$";
        String deleteRegex = "^delete \\d+";
        String findRegex = "^find (.*)";

        // Prep for extracting regex
        Pattern todoPattern = Pattern.compile(todoRegex);
        this.todoMatcher = todoPattern.matcher(input);

        Pattern deadlinePattern = Pattern.compile(deadlineRegex);
        this.deadlineMatcher = deadlinePattern.matcher(input);

        Pattern eventPattern = Pattern.compile(eventRegex);
        this.eventMatcher = eventPattern.matcher(input);

        Pattern findPattern = Pattern.compile(findRegex);
        this.findMatcher = findPattern.matcher(input);

        if (input.equals("bye")) {
            this.command = CommandType.BYE;
        } else if (input.equals("list")) {
            this.command = CommandType.LIST;
        } else if (input.matches(markRegex)) {
            this.command = CommandType.MARK;
        } else if (input.matches(unmarkRegex)) {
            this.command = CommandType.UNMARK;
        } else if (input.matches(deleteRegex)) {
            this.command = CommandType.DELETE;
        } else if (todoMatcher.matches()) {
            this.command = CommandType.TODO;
        } else if (deadlineMatcher.matches()) {
            this.command = CommandType.DEADLINE;
        } else if (eventMatcher.matches()) {
            this.command = CommandType.EVENT;
        } else if (findMatcher.matches()) {
            this.command = CommandType.FIND;
        } else {
            this.command = CommandType.UNKNOWN;
        }
    }


    /**
     * Processes user input by interpreting the parsed command
     * and updating the task list accordingly.
     * Terminates the main {@code while(true)} loop if the {@code BYE} command is given.
     *
     * @param tasks  The current task list.
     * @return {@code false} if the command is {@link CommandType#BYE}, otherwise {@code true}.
     * @throws ChatBotException If the command is unrecognized or the arguments are invalid.
     */
    public String handleInput(TaskList tasks, Ui ui) throws ChatBotException {
        CommandType commandType = this.getCommandType();
        Task addedTask = null;
        List<String> args = this.getArguments();

        switch (commandType) {
            case BYE:
                return ui.endConversation();

            case LIST:
                return ui.listTasks(tasks);

            case MARK:
                Task taskToMark = this.getTask(tasks);
                taskToMark.markAsDone();
                return ui.showMarkedAsDone(taskToMark);

            case UNMARK:
                Task taskToUnmark = this.getTask(tasks);
                taskToUnmark.markAsUndone();
                return ui.showMarkedAsUndone(taskToUnmark);

            case DELETE:
                Task taskToDelete = this.getTask(tasks);
                tasks.deleteTask(taskToDelete);
                return ui.showDeleted(taskToDelete, tasks.getTotalTasks());

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
                return ui.showFindResult(filteredTaskList);
            default:
                throw new ChatBotException(
                        "OOPS!!! I'm sorry, but I don't know what that means :-("
                );
        }

        if (addedTask != null) {
            tasks.addTask(addedTask);
            return ui.showAddedTask(addedTask, tasks.getTotalTasks());
        }
        return "";
    }

    /**
     * Returns the type of command identified from the user input.
     *
     * @return Command type as an enum {@link CommandType}.
     */
    public CommandType getCommandType() {
        return this.command;
    }

    /**
     * Retrieves a specific task from the given task list based on
     * the task number provided in the user input.
     *
     * @param tasks The current {@link TaskList} containing all tasks.
     * @return The task corresponding to the provided index in user input.
     * @throws ChatBotException If the task number is missing,
     *                          not an integer, or out of range.
     */
    public Task getTask(TaskList tasks) throws ChatBotException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            throw new ChatBotException("OOPS!!! You need to specify a task number.");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new ChatBotException("OOPS!!! Task number must be a valid integer.");
        }

        if (taskIndex < 1 || taskIndex > tasks.getTotalTasks()) {
            throw new ChatBotException("OOPS!!! Task does not exist.");
        }

        return tasks.getSpecificTask(taskIndex - 1);
    }

    /**
     * Extracts and returns arguments from the user input based on
     * the identified command type.
     *
     * <ul>
     *     <li>TODO → [description]</li>
     *     <li>DEADLINE → [description, by]</li>
     *     <li>EVENT → [description, from, to]</li>
     *     <li>FIND -> [search term]</li>
     * </ul>
     *
     * @return A list of extracted arguments for the command.
     * @throws ChatBotException If mandatory fields such as description
     *                          are missing or empty.
     */
    public List<String> getArguments() throws ChatBotException {
        List<String> args = new ArrayList<>();
        String description;

        switch (command) {
        case TODO:
            description = this.todoMatcher.group(1).trim();
            if (description.isEmpty()) {
                throw new ChatBotException("OOPS!!! The description of a todo task cannot be empty.");
            }
            args.add(description);
            break;

        case DEADLINE:
            description = this.deadlineMatcher.group(1).trim();
            if (description.isEmpty()) {
                throw new ChatBotException("OOPS!!! The description of a deadline task cannot be empty.");
            }
            String by = this.deadlineMatcher.group(2).trim();
            Collections.addAll(args, description, by);
            break;

        case EVENT:
            description = this.eventMatcher.group(1).trim();
            if (description.isEmpty()) {
                throw new ChatBotException("OOPS!!! The description of an event task cannot be empty.");
            }
            String from = this.eventMatcher.group(2).trim();
            String to = this.eventMatcher.group(3).trim();
            Collections.addAll(args, description, from, to);
            break;
        case FIND:
            String searchTerm = this.findMatcher.group(1).trim();
            if (searchTerm.isEmpty()) {
                throw new ChatBotException("OOPS!!! It looks like you didn’t enter a search term.");
            }
            args.add(searchTerm);
            break;

        default:
            break;
        }

        return args;
    }
}
