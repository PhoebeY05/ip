package chatbot.storage;

import chatbot.exception.ChatBotException;
import chatbot.task.Deadline;
import chatbot.task.Event;
import chatbot.task.Task;
import chatbot.task.TaskList;
import chatbot.task.Todo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {

    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
        Path path = Paths.get(filePath);

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create parent directories: " + e.getMessage());
        }

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            System.err.println("Failed to create file: " + e.getMessage());
        }
    }

    public void saveToStorage(TaskList tasks) {
        File file = new File(this.filePath);

        try (FileWriter writer = new FileWriter(file, false)) { // false = overwrite
            for (Task task : tasks.getTasks()) {
                if (task != null) {
                    writer.write(task + System.lineSeparator());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Task> load() throws ChatBotException {
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            File file = new File(this.filePath);
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if (data.trim().isEmpty()) {
                    break;
                }

                if (data.startsWith("[T]")) {
                    Todo todo = Todo.toTodo(data);
                    tasks.add(todo);
                } else if (data.startsWith("[D]")) {
                    Deadline deadline = Deadline.toDeadline(data);
                    tasks.add(deadline);
                } else if (data.startsWith("[E]")) {
                    Event event = Event.toEvent(data);
                    tasks.add(event);
                } else {
                    throw new ChatBotException("OOPS!! Data file has unknown line!");
                }
            }

            reader.close();
        } catch (Exception e) {
            throw new ChatBotException(e.getMessage());
        }

        return tasks;
    }
}
