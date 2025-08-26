import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Deadline extends Task {

    protected LocalDateTime by;

    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
    }

    public static Deadline toDeadline(String deadline) {
        String regex = "^\\[D]\\[\\s|X]\\s+(.*?)\\s+\\(by:\\s+(.+)\\)$\n";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(deadline);
        if (matcher.matches()) {
            boolean status = matcher.group(1).equals("X");
            String description = matcher.group(2);
            String by = matcher.group(3);

            Deadline deadlineObject = new Deadline(description, by);
            if (status) {
                deadlineObject.markAsDone();
            }
            return deadlineObject;
        }
        return null;
    }

    @Override
    public String toString() {
        String dateBy = this.by.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        return "[D]" + super.toString() + " (by: " + dateBy + ")";
    }
}