import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    public static Todo toTodo(String todo) {
        String regex = "^\\[T]\\[( |X)]\\s+(.*)$\n";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(todo);
        if (matcher.matches()) {
            boolean status = matcher.group(1).equals("X");
            String description = matcher.group(2);
            Todo todoObject = new Todo(description);
            if (status) {
                todoObject.markAsDone();
            }
            return todoObject;
        }
        return null;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
