package chatbot.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.exception.ChatBotException;

/**
 * Represents a task that spans a period of time.
 * An event has a description, completion status, start time, and end time.
 */
public class Event extends Task {

    protected LocalDateTime from; // Event start time
    protected LocalDateTime to;   // Event end time

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm");

    /**
     * Constructs an Event task with the given description, start time (string), and end time (string).
     * Ensures that the start time is not after the end time.
     *
     * @param description Description of the event task.
     * @param from Start time in {@code d/M/yyyy HHmm} format.
     * @param to End time in {@code d/M/yyyy HHmm} format.
     * @throws ChatBotException If the start time is after the end time.
     */
    public Event(String description, String from, String to) throws ChatBotException {
        super(description);
        this.from = LocalDateTime.parse(from, INPUT_FORMAT);
        this.to = LocalDateTime.parse(to, INPUT_FORMAT);

        if (this.from.isAfter(this.to)) {
            throw new ChatBotException(
                    "OOPS!!! An event cannot end before it starts. Please check the dates and try again."
            );
        }
    }

    /**
     * Constructs an Event task with the given description, start time, and end time.
     *
     * @param description Description of the event task.
     * @param from Start time as a {@link LocalDateTime}.
     * @param to End time as a {@link LocalDateTime}.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Converts a serialized string back into an {@link Event} object.
     * The string must match the format produced by {@link #toString()}.
     *
     * @param event Serialized event string.
     * @return An {@link Event} object reconstructed from the string.
     * @throws ChatBotException If the string does not match the expected format.
     */
    public static Event convertToEvent(String event) throws ChatBotException {
        // Regex matches: [E][ ] description (from: Dec 2 2025, 16:00 to: Dec 2 2025, 18:00)
        String regex = "^\\[E]\\[([ X])]\\s+(.*?)\\s+\\(from:\\s+(.+?)\\s+to:\\s+(.+)\\)$";
        Matcher matcher = Pattern.compile(regex).matcher(event);

        if (!matcher.matches()) {
            throw new ChatBotException(
                    "OOPS!! This string cannot be converted to an Event object."
            );
        }

        boolean isDone = matcher.group(1).equals("X");   // Check if event is marked done
        String description = matcher.group(2).trim();    // Extract description
        String fromString = matcher.group(3).trim();     // Extract start time
        String toString = matcher.group(4).trim();       // Extract end time

        LocalDateTime fromDate = LocalDateTime.parse(fromString, OUTPUT_FORMAT);
        LocalDateTime toDate = LocalDateTime.parse(toString, OUTPUT_FORMAT);

        Event eventObject = new Event(description, fromDate, toDate);
        if (isDone) {
            eventObject.markAsDone(); // Restore completion status
        }

        return eventObject;
    }

    /**
     * Returns the string representation of the event task in the format:
     * [E][ ] description (from: Dec 2 2025, 16:00 to: Dec 2 2025, 18:00)
     *
     * @return String representation of the event task.
     */
    @Override
    public String toString() {
        String formattedFrom = this.from.format(OUTPUT_FORMAT);
        String formattedTo = this.to.format(OUTPUT_FORMAT);

        return "[E]" + super.toString() + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }
}
