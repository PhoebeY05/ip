package chatbot.command;

import chatbot.exception.ChatBotException;
import chatbot.task.Task;
import chatbot.task.TaskList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private final String input;
    private final CommandType command;

    private final Matcher todoMatcher;
    private final Matcher deadlineMatcher;
    private final Matcher eventMatcher;

    public Parser(String input) {
        this.input = input;

        String markRegex = "^mark \\d+";
        String unmarkRegex = "^unmark \\d+";
        String todoRegex = "^todo (.*)";
        String deadlineRegex = "^deadline (.*) /by (.+)";
        String eventRegex = "^event (.*) /from (.+) /to (.+)$";
        String deleteRegex = "^delete \\d+";

        // Prep for extracting regex
        Pattern todoPattern = Pattern.compile(todoRegex);
        this.todoMatcher = todoPattern.matcher(input);

        Pattern deadlinePattern = Pattern.compile(deadlineRegex);
        this.deadlineMatcher = deadlinePattern.matcher(input);

        Pattern eventPattern = Pattern.compile(eventRegex);
        this.eventMatcher = eventPattern.matcher(input);

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
        } else {
            this.command = CommandType.UNKNOWN;
        }
    }

    public CommandType getCommandType() {
        return this.command;
    }

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

    public ArrayList<String> getArguments() throws ChatBotException {
        ArrayList<String> args = new ArrayList<>();
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

            default:
                break;
        }

        return args;
    }
}
