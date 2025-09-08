package chatbot.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.exception.ChatBotException;

/**
 * Represents a task of type "Todo".
 * A todo task has only a description and completion status,
 * without any associated date or time.
 */
public class Todo extends Task {

    /**
     * Constructs a Todo task with the given description.
     * The task is initially marked as not done.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Converts a serialized string back into a {@link Todo} object.
     * The string must match the format produced by {@link #toString()}:
     * [T][ ] description
     * [T][X] description
     *
     * @param todoString Serialized todo string.
     * @return A {@link Todo} object reconstructed from the string.
     * @throws ChatBotException If the string does not match the expected format.
     */
    public static Todo convertToTodo(String todoString) throws ChatBotException {
        // Regex to capture completion status and description
        String regex = "^\\[T]\\[([ X])]\\s+(.*)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(todoString);

        if (matcher.matches()) {
            boolean isDone = matcher.group(1).equals("X"); // true if marked done
            String description = matcher.group(2);

            Todo todoObject = new Todo(description);
            if (isDone) {
                todoObject.markAsDone(); // Restore completion status
            }

            return todoObject;
        } else {
            throw new ChatBotException(
                    "OOPS!! This string cannot be converted to a Todo object."
            );
        }
    }

    /**
     * Returns the string representation of the todo task in the format:
     * [T][ ] description
     * [T][X] description
     *
     * @return String representation of the todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString(); // Prefix with [T] for Todo
    }
}
