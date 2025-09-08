package chatbot.task;

/**
 * Represents a generic task.
 * A task has a description and a completion status (done or not done).
 * This class is extended by specific task types such as {@link Todo}, {@link Deadline}, and {@link Event}.
 */
public class Task {

    protected String description;  // Task description
    protected boolean isDone;      // Completion status

    /**
     * Constructs a Task with the given description.
     * The task is initially marked as not done.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false; // Default to not done
    }

    /**
     * Returns the status icon of the task.
     * <ul>
     *     <li>"X" if the task is completed</li>
     *     <li>" " (whitespace) if the task is not completed</li>
     * </ul>
     *
     * @return Status icon string.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " "; // Mark done task with "X"
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns the string representation of the task in the format:
     * [ ] description   // if not done
     * [X] description   // if done
     *
     * @return String representation of the task.
     */
    @Override
    public String toString() {
        // Use getStatusIcon() to show completion status
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }
}
