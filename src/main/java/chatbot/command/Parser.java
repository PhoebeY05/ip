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
     * Executes the command and updates the task list accordingly.
     *
     * @param tasks Current task list.
     * @param ui    UI handler for displaying results.
     * @return Response string for the chatbot.
     * @throws ChatBotException If the command is invalid or arguments are missing.
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
                assert tasks.getTotalTasks() == initial - 1;
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

            case SEARCH_TASK:
                String regex = "\\b" + args.get(0) + "\\b";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                TaskList filteredTaskList = tasks.filter(task -> pattern.matcher(task.toString()).find());
                assert filteredTaskList.getTotalTasks() <= initial;
                return ui.showFindResult(filteredTaskList);

            case FIND_FREE_TIMES:
                int hours = Integer.parseInt(args.get(0));
                if (hours <= 0) {
                    throw new ChatBotException("OOPS!!! Duration must be greater than 0.");
                }

                Instant nearestMin = Instant.now().truncatedTo(ChronoUnit.MINUTES);
                LocalDateTime nowDateTime = nearestMin.atZone(ZoneId.systemDefault()).toLocalDateTime();

                TaskList events = tasks.filter(task -> task instanceof Event);
                TaskList sortedEvents = events.sort(Comparator.comparing(task -> ((Event) task).getFrom()));
                TaskList eventsAfterNow = sortedEvents.filter(task -> ((Event) task).getFrom().isAfter(nowDateTime));

                if (eventsAfterNow.getTotalTasks() == 0) {
                    return nowDateTime + " to " + nowDateTime.plusHours(hours);
                }

                LocalDateTime startDateTime = Parser.getStartOfFreeTime(nowDateTime, eventsAfterNow, hours);
                LocalDateTime endDateTime = startDateTime.plusHours(hours);

                assert !startDateTime.isBefore(nowDateTime);
                return startDateTime.format(OUTPUT_FORMAT) + " to " + endDateTime.format(OUTPUT_FORMAT);

            default:
                throw new ChatBotException("OOPS!!! I don’t know what that means :-(");
        }

        tasks.addTask(addedTask);
        assert tasks.getTotalTasks() == initial + 1;
        return ui.showAddedTask(addedTask, tasks.getTotalTasks());
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
