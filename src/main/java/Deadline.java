import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {

    protected LocalDateTime by;

    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
    }

    @Override
    public String toString() {
        String dateBy = this.by.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        return "[D]" + super.toString() + " (by: " + dateBy + ")";
    }
}