import java.lang.reflect.Array;
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

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public int getTotalTasks() {
        return this.tasks.size();
    }

    public Task getSpecificTask(int i) {
        return this.tasks.get(i);
    }

    @Override
    public String toString() {
        StringBuilder tasks = new StringBuilder();
        for (int i = 0; i < this.tasks.size(); i++) {
            Task curr = this.tasks.get(i);
            tasks.append(String.format("%d.%s", i + 1, curr.toString()));
            if (i < this.tasks.size() - 1) {
                tasks.append("\n");
            }
        }
        return tasks.toString();
    }
}
