package chatbot.command;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.exception.ChatBotException;
import chatbot.task.*;
import chatbot.ui.Ui;

/**
 * Parses user input into chatbot commands and executes them.
 * Supports adding, modifying, deleting, searching, and listing tasks,
 * as well as finding free time and exiting the application.
 */
public class Parser {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm");

    private final String input;
    private final CommandType command;

    private final Matcher todoMatcher;
    private final Matcher deadlineMatcher;
    private final Matcher eventMatcher;
    private final Matcher searchMatcher;
    private final Matcher freeTimeMatcher;

    /**
     * Creates a parser for the given user input.
     * Identifies the command type and prepares regex matchers.
     *
     * @param input Raw user input.
     */
    public Parser(String input) {
        this.input = input;

        // Regex patterns for supported commands
        String markRegex = "^mark \\d+";
        String unmarkRegex = "^unmark \\d+";
        String todoRegex = "^todo (.*)";
        String deadlineRegex = "^deadline (.*) /by (.+)";
        String eventRegex = "^event (.*) /from (.+) /to (.+)$";
        String deleteRegex = "^delete \\d+";
        String searchRegex = "^find (.*)";
        String freeTimeRegex = "^free /duration (.*)";

        // Compile matchers for extracting arguments
        this.todoMatcher = Pattern.compile(todoRegex).matcher(input);
        this.deadlineMatcher = Pattern.compile(deadlineRegex).matcher(input);
        this.eventMatcher = Pattern.compile(eventRegex).matcher(input);
        this.searchMatcher = Pattern.compile(searchRegex).matcher(input);
        this.freeTimeMatcher = Pattern.compile(freeTimeRegex).matcher(input);

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
        } else if (searchMatcher.matches()) {
            this.command = CommandType.SEARCH_TASK;
        } else if (freeTimeMatcher.matches()) {
            this.command = CommandType.FIND_FREE_TIMES;
        } else {
            this.command = CommandType.UNKNOWN;
        }
    }

    // Used ChatGPT to make handleInput adhere to SLAP and write JavaDoc comments for new functions

    /**
     * Finds the earliest free time slot of the given duration
     * starting from the provided reference time.
     *
     * @param identity Reference time.
     * @param tasks    List of tasks (must be sorted).
     * @param hours    Duration of free time needed.
     * @return Start time of the available free slot.
     */
    public static LocalDateTime getStartOfFreeTime(LocalDateTime identity, TaskList tasks, int hours) {
        LocalDateTime result = identity;
        for (Task t : tasks.getTasks()) {
            Event event = (Event) t;
            if (!event.getFrom().minusHours(hours).isBefore(result)) {
                break;
            } else {
                result = event.getTo();
            }
        }
        return result;
    }

    /**
     * Executes a chatbot command and updates the task list accordingly.
     *
     * @param tasks Current task list containing all stored tasks.
     * @param ui    UI handler used to generate user-facing responses.
     * @return A response string to be displayed to the user.
     * @throws ChatBotException If the command type is unknown or arguments are invalid.
     */
    public String handleInput(TaskList tasks, Ui ui) throws ChatBotException {
        CommandType commandType = this.getCommandType();

        return switch (commandType) {
            case EXIT -> ui.endConversation();
            case LIST_TASKS -> ui.listTasks(tasks);
            case MARK_TASK -> handleMarkTask(tasks, ui);
            case UNMARK_TASK -> handleUnmarkTask(tasks, ui);
            case DELETE_TASK -> handleDeleteTask(tasks, ui);
            case ADD_TODO, ADD_DEADLINE, ADD_EVENT -> handleAddTask(tasks, ui, commandType);
            case SEARCH_TASK -> handleSearchTask(tasks, ui);
            case FIND_FREE_TIMES -> handleFindFreeTimes(tasks, ui);
            default -> throw new ChatBotException("OOPS!!! I don’t know what that means :-(");
        };
    }

    /**
     * Marks a task as done and returns a confirmation message.
     */
    private String handleMarkTask(TaskList tasks, Ui ui) throws ChatBotException {
        Task task = this.getTask(tasks);
        task.markAsDone();
        assert task.getStatusIcon().equals("X");
        return ui.showMarkedAsDone(task);
    }

    /**
     * Unmarks a task (sets it back to not done).
     */
    private String handleUnmarkTask(TaskList tasks, Ui ui) throws ChatBotException {
        Task task = this.getTask(tasks);
        task.markAsUndone();
        assert task.getStatusIcon().equals(" ");
        return ui.showMarkedAsUndone(task);
    }

    /**
     * Deletes a task from the task list.
     */
    private String handleDeleteTask(TaskList tasks, Ui ui) throws ChatBotException {
        int initial = tasks.getTotalTasks();
        Task task = this.getTask(tasks);
        tasks.deleteTask(task);
        validateTaskListChange(initial, tasks.getTotalTasks(), -1);
        return ui.showDeleted(task, tasks.getTotalTasks());
    }

    /**
     * Adds a new task (Todo, Deadline, or Event) to the task list.
     */
    private String handleAddTask(TaskList tasks, Ui ui, CommandType commandType) throws ChatBotException {
        int initial = tasks.getTotalTasks();
        Task addedTask = createTaskFromArgs(commandType, this.getArguments());

        tasks.addTask(addedTask);
        validateTaskListChange(initial, tasks.getTotalTasks(), +1);
        return ui.showAddedTask(addedTask, tasks.getTotalTasks());
    }

    /**
     * Creates a task object based on the given command type and arguments.
     */
    private Task createTaskFromArgs(CommandType commandType, List<String> args) throws ChatBotException {
        return switch (commandType) {
            case ADD_TODO -> new Todo(args.get(0));
            case ADD_DEADLINE -> new Deadline(args.get(0), args.get(1));
            case ADD_EVENT -> new Event(args.get(0), args.get(1), args.get(2));
            default -> throw new ChatBotException("Invalid add command.");
        };
    }

    /**
     * Searches for tasks matching a given keyword.
     */
    private String handleSearchTask(TaskList tasks, Ui ui) throws ChatBotException {
        List<String> args = this.getArguments();
        int initial = tasks.getTotalTasks();

        String regex = "\\b" + args.get(0) + "\\b";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        TaskList filteredTaskList = tasks.filter(task -> pattern.matcher(task.toString()).find());

        assert filteredTaskList.getTotalTasks() <= initial;
        return ui.showFindResult(filteredTaskList);
    }

    /**
     * Finds and returns the nearest available free time slot of a given duration.
     *
     * @param tasks Task list containing scheduled events.
     * @param ui    UI handler for formatting the free time response.
     * @return A UI-formatted string showing the available free time range.
     * @throws ChatBotException If the requested duration is invalid or arguments are missing.
     */
    private String handleFindFreeTimes(TaskList tasks, Ui ui) throws ChatBotException {
        List<String> args = this.getArguments();
        int hours = Integer.parseInt(args.get(0));

        if (hours <= 0) {
            throw new ChatBotException("OOPS!!! Duration must be greater than 0.");
        }

        Instant nearestMin = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime nowDateTime = nearestMin.atZone(ZoneId.systemDefault()).toLocalDateTime();

        TaskList sortedEvents = getSortedFutureEvents(tasks, nowDateTime);

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        if (sortedEvents.getTotalTasks() == 0) {
            // If no events, free time starts immediately
            startDateTime = nowDateTime;
            endDateTime = nowDateTime.plusHours(hours);
        } else {
            // Otherwise calculate next available slot
            startDateTime = Parser.getStartOfFreeTime(nowDateTime, sortedEvents, hours);
            endDateTime = startDateTime.plusHours(hours);
        }

        assert !startDateTime.isBefore(nowDateTime);
        return formatFreeTimeRange(startDateTime, endDateTime, ui);
    }

    /**
     * Retrieves all future events starting after the given reference time,
     * and returns them sorted by their start time.
     *
     * @param tasks      Task list containing events.
     * @param reference  The current date-time used as a cutoff for filtering.
     * @return A TaskList of events occurring after the reference time, sorted chronologically.
     */
    private TaskList getSortedFutureEvents(TaskList tasks, LocalDateTime reference) {
        TaskList futureEvents = tasks.filter(task ->
                task instanceof Event && ((Event) task).getFrom().isAfter(reference));
        return futureEvents.sort(Comparator.comparing(task -> ((Event) task).getFrom()));
    }

    /**
     * Formats the free time range into a UI-friendly string.
     *
     * @param start Start of the free time slot.
     * @param end   End of the free time slot.
     * @param ui    UI handler used to generate the formatted output.
     * @return A string formatted for user display.
     */
    private String formatFreeTimeRange(LocalDateTime start, LocalDateTime end, Ui ui) {
        return ui.showFreeTimeRange(
                start.format(OUTPUT_FORMAT),
                end.format(OUTPUT_FORMAT)
        );
    }

    /**
     * Validates that the task list size has changed by the expected delta.
     */
    private void validateTaskListChange(int initialCount, int currentCount, int expectedChange) {
        assert currentCount == initialCount + expectedChange :
                "Expected task list size to change by " + expectedChange
                        + " but changed by " + (currentCount - initialCount);
    }


    /**
     * Returns the detected command type.
     *
     * @return Command type.
     */
    public CommandType getCommandType() {
        return this.command;
    }

    /**
     * Retrieves a task from the task list based on user input index.
     *
     * @param tasks Current task list.
     * @return Task at the specified index.
     * @throws ChatBotException If the index is missing, invalid, or out of bounds.
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
     * Extracts arguments from the user input based on the command type.
     *
     * @return List of arguments.
     * @throws ChatBotException If mandatory arguments are missing.
     */
    public List<String> getArguments() throws ChatBotException {
        List<String> args = new ArrayList<>();
        String description;

        switch (command) {
            case ADD_TODO:
                description = this.todoMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! Todo description cannot be empty.");
                }
                args.add(description);
                break;

            case ADD_DEADLINE:
                description = this.deadlineMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! Deadline description cannot be empty.");
                }
                String by = this.deadlineMatcher.group(2).trim();
                Collections.addAll(args, description, by);
                break;

            case ADD_EVENT:
                description = this.eventMatcher.group(1).trim();
                if (description.isEmpty()) {
                    throw new ChatBotException("OOPS!!! Event description cannot be empty.");
                }
                String from = this.eventMatcher.group(2).trim();
                String to = this.eventMatcher.group(3).trim();
                Collections.addAll(args, description, from, to);
                break;

            case SEARCH_TASK:
                String searchTerm = this.searchMatcher.group(1).trim();
                if (searchTerm.isEmpty()) {
                    throw new ChatBotException("OOPS!!! You need to enter a search term.");
                }
                args.add(searchTerm);
                break;

            case FIND_FREE_TIMES:
                String hours = this.freeTimeMatcher.group(1).trim();
                if (hours.isEmpty()) {
                    throw new ChatBotException("OOPS!!! You need to enter a duration.");
                }
                args.add(hours);
                break;

            default:
                break;
        }
        return args;
    }
}
