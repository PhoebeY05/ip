package chatbot.task;

import chatbot.exception.ChatBotException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a task that spans a period of time.
 * An event has a description, completion status, start time, and end time.
 */
public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Constructs an Event task with the given description, start time (string), and end time (string).
     * The time strings must match the format {@code d/M/yyyy HHmm}, e.g. {@code 2/12/2025 1600}.
     * <p>
     * Ensures that the start time is not after the end time.
     *
     * @param description Description of the event task.
     * @param from Start time in {@code d/M/yyyy HHmm} format.
     * @param to End time in {@code d/M/yyyy HHmm} format.
     * @throws ChatBotException If the start time is after the end time.
     */
    public Event(String description, String from, String to) throws ChatBotException {
        super(description);
        this.from = LocalDateTime.parse(from, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
        this.to = LocalDateTime.parse(to, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
        if (this.from.isAfter(this.to)) {
            throw new ChatBotException("OOPS!!! An event cannot end before it starts. Please check the dates and try again.");
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
     * The string must match the format produced by {@link #toString()}:
     * <pre>
     * [E][ ] description (from: Dec 2 2025, 16:00 to: Dec 2 2025, 18:00)
     * [E][X] description (from: Dec 2 2025, 16:00 to: Dec 2 2025, 18:00)
     * </pre>
     *
     * @param event Serialized event string.
     * @return An {@link Event} object reconstructed from the string.
     * @throws ChatBotException If the string does not match the expected format.
     */
    public static Event toEvent(String event) throws ChatBotException {
        String regex = "^\\[E]\\[([ X])]\\s+(.*?)\\s+\\(from:\\s+(.+?)\\s+to:\\s+(.+)\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(event);
        if (matcher.matches()) {
            boolean status = matcher.group(1).equals("X");
            String description = matcher.group(2);
            String from = matcher.group(3);
            String to = matcher.group(4);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm");
            LocalDateTime fromDate = LocalDateTime.parse(from, formatter);
            LocalDateTime toDate = LocalDateTime.parse(to, formatter);

            Event eventObject = new Event(description, fromDate, toDate);
            if (status) {
                eventObject.markAsDone();
            }
            return eventObject;

        } else {
            throw new ChatBotException("OOPS!! This string cannot be converted to an Event object.");
        }
    }

    /**
     * Returns the string representation of the event task in the format:
     * <pre>
     * [E][ ] description (from: Dec 2 2025, 16:00 to: Dec 2 2025, 18:00)
     * [E][X] description (from: Dec 2 2025, 16:00 to: Dec 2 2025, 18:00)
     * </pre>
     *
     * @return String representation of the event task.
     */
    @Override
    public String toString() {
        String dateFrom = this.from.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        String dateTo = this.to.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        return "[E]" + super.toString() + " (from: " + dateFrom + " to: " + dateTo + ")";
    }
}
