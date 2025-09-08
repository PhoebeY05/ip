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

        // Regex patterns for task parsing and commands
        String markRegex = "^mark \\d+";
        String unmarkRegex = "^unmark \\d+";
        String todoRegex = "^todo (.*)";
        String deadlineRegex = "^deadline (.*) /by (.+)";
        String eventRegex = "^event (.*) /from (.+) /to (.+)$";
        String deleteRegex = "^delete \\d+";
        String findRegex = "^find (.*)";

        // Compile patterns and create matchers for later argument extraction
        this.todoMatcher = Pattern.compile(todoRegex).matcher(input);
        this.deadlineMatcher = Pattern.compile(deadlineRegex).matcher(input);
        this.eventMatcher = Pattern.compile(eventRegex).matcher(input);
        this.findMatcher = Pattern.compile(findRegex).matcher(input);

        // Determine command type
        if (input.equals("bye")) {
            this.command = CommandType.EXIT;
        } else if (input.equals("list")) {
            this.command = CommandType.LIST_TASKS;
        } else if (input.matches(markRegex)) {
            this.command = CommandType.MARK_TASK;
        } else if (input.matches(unmarkRegex)) {
            this.command = CommandType.UNMARK_TASK;
        } else if (input.matches(deleteRegex)) {
            this.command = CommandType.DELETE_TASK;
        } else if (todoMatcher.matches()) {
            this.command = CommandType.ADD_TODO;
        } else if (deadlineMatcher.matches()) {
            this.command = CommandType.ADD_DEADLINE;
        } else if (eventMatcher.matches()) {
            this.command = CommandType.ADD_EVENT;
        } else if (findMatcher.matches()) {
            this.command = CommandType.FIND_TASK;
        } else {
            this.command = CommandType.UNKNOWN;
        }
    }

    /**
     * Processes user input by interpreting the parsed command
     * and updating the task list accordingly.
     *
     * @param tasks  The current task list.
     * @param ui     The UI handler for displaying responses.
     * @return The response string for the chatbot.
     * @throws ChatBotException If the command is unrecognized or arguments are invalid.
     */
    public String handleInput(TaskList tasks, Ui ui) throws ChatBotException {
        CommandType commandType = this.getCommandType();
        Task addedTask;
        List<String> args = this.getArguments();

        int initial = tasks.getTotalTasks();

        switch (commandType) {
            case EXIT:
                return ui.endConversation();

            case LIST_TASKS:
                return ui.listTasks(tasks);

            case MARK_TASK:
                Task taskToMark = this.getTask(tasks);
                taskToMark.markAsDone();
                assert taskToMark.getStatusIcon().equals("X");
                return ui.showMarkedAsDone(taskToMark);

            case UNMARK_TASK:
                Task taskToUnmark = this.getTask(tasks);
                taskToUnmark.markAsUndone();
                assert taskToUnmark.getStatusIcon().equals(" ");
                return ui.showMarkedAsUndone(taskToUnmark);

            case DELETE_TASK:
                Task taskToDelete = this.getTask(tasks);
                tasks.deleteTask(taskToDelete);
                int afterDelete = tasks.getTotalTasks();
                assert afterDelete == initial - 1;
                return ui.showDeleted(taskToDelete, tasks.getTotalTasks());

            case ADD_TODO:
                addedTask = new Todo(args.get(0));
                break;

            case ADD_DEADLINE:
                addedTask = new Deadline(args.get(0), args.get(1));
                break;

            case ADD_EVENT:
                addedTask = new Event(args.get(0), args.get(1), args.get(2));
                break;

            case FIND_TASK:
                String regex = "\\b" + args.get(0) + "\\b";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                TaskList filteredTaskList = tasks.filter(task -> pattern.matcher(task.toString()).find());
                int afterFind = filteredTaskList.getTotalTasks();
                assert afterFind <= initial;
                return ui.showFindResult(filteredTaskList);

            default:
                throw new ChatBotException(
                        "OOPS!!! I'm sorry, but I don't know what that means :-("
                );
        }

        tasks.addTask(addedTask);
        int afterAdd = tasks.getTotalTasks();
        assert afterAdd == initial + 1;
        return ui.showAddedTask(addedTask, tasks.getTotalTasks());
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
     * Retrieves a specific task from the task list based on input index.
     *
     * @param tasks The current {@link TaskList}.
     * @return The task corresponding to the provided index.
     * @throws ChatBotException If the task number is missing, invalid, or out of range.
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
     * Extracts and returns arguments from the user input based on command type.
     *
     * @return A list of extracted arguments for the command.
     * @throws ChatBotException If mandatory fields are missing or empty.
     */
    public List<String> getArguments() throws ChatBotException {
        List<String> args = new ArrayList<>();
        String description;

        switch (command) {
            case ADD_TODO:
                description = this.todoMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! The description of a todo task cannot be empty.");
                }
                args.add(description);
                break;

            case ADD_DEADLINE:
                description = this.deadlineMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! The description of a deadline task cannot be empty.");
                }
                String by = this.deadlineMatcher.group(2).trim();
                Collections.addAll(args, description, by);
                break;

            case ADD_EVENT:
                description = this.eventMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! The description of an event task cannot be empty.");
                }
                String from = this.eventMatcher.group(2).trim();
                String to = this.eventMatcher.group(3).trim();
                Collections.addAll(args, description, from, to);
                break;

            case FIND_TASK:
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
