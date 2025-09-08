package chatbot.command;

/**
 * Represents the types of commands that the chatbot can recognize.
 */
public enum CommandType {
    EXIT,           // Ends the chatbot session
    LIST_TASKS,     // Displays all tasks
    MARK_TASK,      // Marks a task as complete
    UNMARK_TASK,    // Marks a task as incomplete
    DELETE_TASK,    // Deletes a task
    ADD_TODO,       // Adds a "to-do" task
    ADD_DEADLINE,   // Adds a "deadline" task
    ADD_EVENT,      // Adds an "event" task
    FIND_TASK,   // Finds tasks matching a keyword
    UNKNOWN         // Represents an unrecognized command
}