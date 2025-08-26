import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public TaskList() {
        this(new ArrayList<Task>());
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }
}
