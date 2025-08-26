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
            throw new ChatBotException("OOPS!!! An event cannot end before it starts. Please check the dates and try again.");
        }
    }

    public static Event toEvent(String event) {
        String regex = "^\\[E]\\[([ X])]\\s+(.*?)\\s+\\(from:\\s+(.+?)\\s+to:\\s+(.+)\\)$\n";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(event);
        if (matcher.matches()) {
            boolean status = matcher.group(1).equals("X");
            String description = matcher.group(2);
            String from = matcher.group(3);
            String to = matcher.group(4);
            try {
                Event eventObject = new Event(description, from, to);
                if (status) {
                    eventObject.markAsDone();
                }
                return eventObject;
            } catch (ChatBotException e) {
                System.out.println(e.getMessage());
            }

        }
        return null;
    }

    @Override
    public String toString() {
        String dateFrom = this.from.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        String dateTo = this.to.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        return "[E]" + super.toString() + " (from: " + dateFrom + " to: " + dateTo + ")";
    }
}
