package chatbot.command;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    public void getArguments_eventMissingArguments_emptyArray() {
        Parser parser = new Parser("event project meeting /from  /to ");
        try {
            ArrayList<String> args = parser.getArguments();
            assertEquals(new ArrayList<String>(), args);
        } catch (Exception e) {
            fail();
        }
    }
}