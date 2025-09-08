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

    protected LocalDateTime by; // Deadline date and time

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm");

    /**
     * Constructs a Deadline task with the given description and deadline datetime string.
     *
     * @param description Description of the deadline task.
     * @param by Deadline string in {@code d/M/yyyy HHmm} format.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, INPUT_FORMAT);
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
     * The string must match the format produced by {@link #toString()}.
     *
     * @param deadline Serialized deadline string.
     * @return A {@link Deadline} object reconstructed from the string.
     * @throws ChatBotException If the string does not match the expected format.
     */
    public static Deadline convertToDeadline(String deadline) throws ChatBotException {
        // Regex matches: [D][ ] description (by: Dec 2 2025, 18:00)
        String regex = "^\\[D]\\[([ X])]\\s+(.*?)\\s+\\(by:\\s+(.+)\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(deadline);

        if (!matcher.matches()) {
            throw new ChatBotException(
                    "OOPS!! This string cannot be converted to a Deadline object."
            );
        }

        boolean isDone = matcher.group(1).equals("X");    // Check if task is marked done
        String description = matcher.group(2).trim();     // Extract description
        String byString = matcher.group(3).trim();        // Extract date string

        LocalDateTime byDate = LocalDateTime.parse(byString, OUTPUT_FORMAT); // Parse to LocalDateTime
        Deadline deadlineObject = new Deadline(description, byDate);

        if (isDone) {
            deadlineObject.markAsDone(); // Restore completion status
        }

        return deadlineObject;
    }

    /**
     * Returns the string representation of the deadline task in the format:
     * [D][ ] description (by: Dec 2 2025, 18:00)
     *
     * @return String representation of the deadline task.
     */
    @Override
    public String toString() {
        String formattedBy = this.by.format(OUTPUT_FORMAT);
        return "[D]" + super.toString() + " (by: " + formattedBy + ")";
    }
}
