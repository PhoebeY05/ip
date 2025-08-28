package chatbot.task;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EventTest {
    @Test
    public void stringToDeadline_eventMissingArguments_exceptionThrown() {
        String input = "event project meeting /from  /to ";
        try {
            Event event = Event.toEvent(input);
            fail();
        } catch (Exception e) {
            assertEquals("OOPS!! This string cannot be converted to an Event object.", e.getMessage());
        }
    }
}
