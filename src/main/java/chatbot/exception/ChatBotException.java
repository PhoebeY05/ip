package chatbot.exception;

/**
 * Represents a custom exception used in the ChatBot application.
 * This exception is thrown when an error specific to ChatBot occurs,
 * such as invalid user input or issues during file operations.
 */
public class ChatBotException extends Exception {

    /**
     * Constructs a {@code ChatBotException} with the specified detail message.
     *
     * @param msg The detail message describing the cause of the exception.
     */
    public ChatBotException(String msg) {
        super(msg);
    }
}
