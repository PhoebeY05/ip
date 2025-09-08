package chatbot.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chatbot.exception.ChatBotException;

/**
 * Represents a task that spans a period of time.
 * An event has a description, completion status, start time, and end time.
 */
public class Event extends Task {

    /** Start time of the event. */
    protected LocalDateTime from;

    /** End time of the event. */
    protected LocalDateTime to;

    /** Formatter for parsing user input dates. */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /** Formatter for displaying dates in task output. */
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm");

    /**
     * Constructs an Event task with the given description and time range.
     *
     * @param description Description of the event.
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
     * Constructs an Event task with the given description and time range.
     *
     * @param description Description of the event.
     * @param from Start time.
     * @param to End time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Converts a serialized event string back into an {@link Event}.
     *
     * @param event Serialized event string.
     * @return Reconstructed Event object.
     * @throws ChatBotException If the string does not match the expected format.
     */
    public static Event convertToEvent(String event) throws ChatBotException {
        String regex = "^\\[E]\\[([ X])]\\s+(.*?)\\s+\\(from:\\s+(.+?)\\s+to:\\s+(.+)\\)$";
        Matcher matcher = Pattern.compile(regex).matcher(event);

        if (!matcher.matches()) {
            throw new ChatBotException("OOPS!! This string cannot be converted to an Event object.");
        }

        boolean isDone = matcher.group(1).equals("X");
        String description = matcher.group(2).trim();
        String fromString = matcher.group(3).trim();
        String toString = matcher.group(4).trim();

        LocalDateTime fromDate = LocalDateTime.parse(fromString, OUTPUT_FORMAT);
        LocalDateTime toDate = LocalDateTime.parse(toString, OUTPUT_FORMAT);

        Event eventObject = new Event(description, fromDate, toDate);
        if (isDone) {
            eventObject.markAsDone();
        }

        return eventObject;
    }

    /**
     * Returns the start time of the event.
     *
     * @return Start time.
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the end time of the event.
     *
     * @return End time.
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Merges overlapping events from a sorted task list into continuous time ranges.
     * <p>Assumes the given task list contains only {@link Event} objects, sorted by start time.</p>
     *
     * @param sorted Task list of events, sorted by start time.
     * @return List of merged time ranges as {@code LocalDateTime[]} pairs.
     */
    public static ArrayList<LocalDateTime[]> mergeOverlappingEvents(TaskList sorted) {
        int n = sorted.getTotalTasks();
        ArrayList<LocalDateTime[]> merged = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            Task task = sorted.getSpecificTask(i);
            assert task instanceof Event : "Non-event task found in mergeOverlappingEvents";

            Event event = (Event) task;
            LocalDateTime start = event.getFrom();
            LocalDateTime end = event.getTo();

            // Skip if already covered by previous merged interval
            if (!merged.isEmpty() && !merged.get(merged.size() - 1)[1].isBefore(end)) {
                continue;
            }

            // Extend the interval with overlapping events
            for (int j = i + 1; j < n; j++) {
                Task nextTask = sorted.getSpecificTask(j);
                assert nextTask instanceof Event : "Non-event task found in mergeOverlappingEvents";

                Event nextEvent = (Event) nextTask;
                if (nextEvent.getFrom().isAfter(end)) {
                    break; // no overlap
                }
                end = end.isAfter(nextEvent.getTo()) ? end : nextEvent.getTo();
            }

            merged.add(new LocalDateTime[]{start, end});
        }
        return merged;
    }

    /**
     * Returns the string representation of the event.
     * Example:
     * <pre>
     * [E][ ] project meeting (from: Dec 2 2025, 16:00 to: Dec 2 2025, 18:00)
     * </pre>
     *
     * @return String representation of the event.
     */
    @Override
    public String toString() {
        String formattedFrom = this.from.format(OUTPUT_FORMAT);
        String formattedTo = this.to.format(OUTPUT_FORMAT);
        return "[E]" + super.toString() + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }
}
