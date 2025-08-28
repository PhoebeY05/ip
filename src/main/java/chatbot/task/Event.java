package chatbot.task;

import chatbot.exception.ChatBotException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, String from, String to) throws ChatBotException {
        super(description);
        this.from = LocalDateTime.parse(from, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
        this.to = LocalDateTime.parse(to, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));

        if (this.from.isAfter(this.to)) {
            throw new ChatBotException(
                    "OOPS!!! An event cannot end before it starts. Please check the dates and try again."
            );
        }
    }

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public static Event toEvent(String event) throws ChatBotException {
        String regex = "^\\[E]\\[([ X])]\\s+(.*?)\\s+\\(from:\\s+(.+?)\\s+to:\\s+(.+)\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(event);

        if (matcher.matches()) {
            boolean isDone = matcher.group(1).equals("X");
            String description = matcher.group(2);
            String fromString = matcher.group(3);
            String toString = matcher.group(4);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm");
            LocalDateTime fromDate = LocalDateTime.parse(fromString, formatter);
            LocalDateTime toDate = LocalDateTime.parse(toString, formatter);

            Event eventObject = new Event(description, fromDate, toDate);
            if (isDone) {
                eventObject.markAsDone();
            }

            return eventObject;
        } else {
            throw new ChatBotException(
                    "OOPS!! This string cannot be converted to an Event object."
            );
        }
    }

    @Override
    public String toString() {
        String formattedFrom = this.from.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        String formattedTo = this.to.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));

        return "[E]" + super.toString() + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }
}