package chatbot.task;

import java.util.ArrayList;

public class TaskList {

    private final ArrayList<Task> tasks;

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public TaskList() {
        this(new ArrayList<>());
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

    public Task getSpecificTask(int index) {
        return this.tasks.get(index);
    }

    @Override
    public String toString() {
        StringBuilder tasksString = new StringBuilder();

        for (int i = 0; i < this.getTotalTasks(); i++) {
            Task currentTask = this.tasks.get(i);
            tasksString.append(String.format("%d.%s", i + 1, currentTask.toString()));

            if (i < this.tasks.size() - 1) {
                tasksString.append("\n");
            }
        }

        return tasksString.toString();
    }
}