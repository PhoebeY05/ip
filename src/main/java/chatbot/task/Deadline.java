package chatbot.task;

import chatbot.exception.ChatBotException;

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

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    public static Deadline toDeadline(String deadline) throws ChatBotException {
        String regex = "^\\[D]\\[([ X])]\\s+(.*?)\\s+\\(by:\\s+(.+)\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(deadline);
        if (matcher.matches()) {
            boolean status = matcher.group(1).equals("X");
            String description = matcher.group(2);
            String by = matcher.group(3);
            LocalDateTime byDate = LocalDateTime.parse(by, DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));

            Deadline deadlineObject = new Deadline(description, byDate);
            if (status) {
                deadlineObject.markAsDone();
            }
            return deadlineObject;
        } else {
            throw new ChatBotException("OOPS!! This string cannot be converted to a Deadline object.");
        }
    }

    @Override
    public String toString() {
        String dateBy = this.by.format(DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm"));
        return "[D]" + super.toString() + " (by: " + dateBy + ")";
    }
}