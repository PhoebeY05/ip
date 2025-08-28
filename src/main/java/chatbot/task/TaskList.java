package chatbot.task;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Represents a list of {@link Task} objects.
 * Provides methods to add, remove, retrieve, and display tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs a TaskList with a predefined list of tasks.
     *
     * @param tasks An {@link ArrayList} of tasks to initialize the list.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Adds a task to the task list.
     *
     * @param task The {@link Task} to be added.
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * Deletes a task from the task list.
     *
     * @param task The {@link Task} to be removed.
     */
    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }

    /**
     * Returns the list of all tasks.
     *
     * @return An {@link ArrayList} of {@link Task} objects.
     */
    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return Total number of tasks.
     */
    public int getTotalTasks() {
        return this.tasks.size();
    }

    /**
     * Retrieves a task at the specified index.
     *
     * @param i Zero-based index of the task.
     * @return The {@link Task} at the given index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task getSpecificTask(int i) {
        return this.tasks.get(i);
    }

    /**
     * Returns a new {@link TaskList} containing only the tasks that match
     * the given predicate.
     *
     * @param predicate A {@link Predicate} to test each task.
     * @return A new {@link TaskList} with tasks that satisfy the predicate.
     */
    public TaskList filter(Predicate<Task> predicate) {
        Stream<Task> stream = this.tasks.stream().filter(predicate);
        ArrayList<Task> taskList = new ArrayList<>(stream.toList());
        return new TaskList(taskList);
    }

    /**
     * Returns the string representation of the task list,
     * where each task is displayed on a new line with its index.
     * <pre>
     * 1.[T][ ] read book
     * 2.[D][X] submit assignment (by: Dec 2 2025, 18:00)
     * </pre>
     *
     * @return String representation of the task list.
     */
    @Override
    public String toString() {
        StringBuilder tasks = new StringBuilder();
        for (int i = 0; i < this.getTotalTasks(); i++) {
            Task curr = this.tasks.get(i);
            tasks.append(String.format("%d.%s", i + 1, curr.toString()));
            if (i < this.tasks.size() - 1) {
                tasks.append("\n");
            }
        }
        return tasks.toString();
    }
}
