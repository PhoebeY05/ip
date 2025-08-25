import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDateTime.parse(from, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
        this.to = LocalDateTime.parse(to, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
    }

    @Override
    public String toString() {
        String dateFrom = this.from.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        String dateTo = this.to.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        return "[E]" + super.toString() + " (from: " + dateFrom + " to: " + dateTo + ")";
    }
}
