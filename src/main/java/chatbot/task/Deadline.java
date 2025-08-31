package chatbot.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.exception.ChatBotException;


/**
 * Represents a task with a deadline.
 * A deadline task has a description, completion status, and a specific datetime by which it must be done.
 */
public class Deadline extends Task {

    protected LocalDateTime by;

    /**
     * Constructs a Deadline task with the given description and deadline datetime.
     * The deadline string must match the format {@code d/M/yyyy HHmm}, e.g. {@code 2/12/2025 1800}.
     *
     * @param description Description of the deadline task.
     * @param by Deadline string in {@code d/M/yyyy HHmm} format.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
    }

    /**
     * Constructs a Deadline task with the given description and {@link LocalDateTime} object.
     *
     * @param description Description of the deadline task.
     * @param by Deadline date/time as a {@link LocalDateTime}.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Converts a serialized string back into a {@link Deadline} object.
     * The string must match the format produced by {@link #toString()}:
     * <pre>
     * [D][ ] description (by: Dec 2 2025, 18:00)
     * [D][X] description (by: Dec 2 2025, 18:00)
     * </pre>
     *
     * @param deadline Serialized deadline string.
     * @return A {@link Deadline} object reconstructed from the string.
     * @throws ChatBotException If the string does not match the expected format.
     */
    public static Deadline toDeadline(String deadline) throws ChatBotException {
        String regex = "^\\[D]\\[([ X])]\\s+(.*?)\\s+\\(by:\\s+(.+)\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(deadline);

        if (matcher.matches()) {
            boolean isDone = matcher.group(1).equals("X");
            String description = matcher.group(2);
            String byString = matcher.group(3);
            LocalDateTime byDate = LocalDateTime.parse(
                    byString, DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm")
            );

            Deadline deadlineObject = new Deadline(description, byDate);
            if (isDone) {
                deadlineObject.markAsDone();
            }

            return deadlineObject;
        } else {
            throw new ChatBotException(
                    "OOPS!! This string cannot be converted to a Deadline object."
            );
        }
    }

    /**
     * Returns the string representation of the deadline task in the format:
     * <pre>
     * [D][ ] description (by: Dec 2 2025, 18:00)
     * [D][X] description (by: Dec 2 2025, 18:00)
     * </pre>
     *
     * @return String representation of the deadline task.
     */
    @Override
    public String toString() {
        String formattedBy = this.by.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        return "[D]" + super.toString() + " (by: " + formattedBy + ")";
    }
}
