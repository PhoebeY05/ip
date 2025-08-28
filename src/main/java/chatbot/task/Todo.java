package chatbot.task;

import chatbot.exception.ChatBotException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    public static Todo toTodo(String todo) throws ChatBotException {
        String regex = "^\\[T]\\[([ X])]\\s+(.*)$";
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
        } else {
            throw new ChatBotException("OOPS!! This string cannot be converted to a Todo object.");
        }
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
