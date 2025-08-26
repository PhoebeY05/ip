import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        File f = new File(this.filePath);
        try (FileWriter fw = new FileWriter(f, false)) { // false = overwrite
            for (Task t : tasks.getTasks()) {
                fw.write(t + System.lineSeparator());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public TaskList loadFromFile() {
        TaskList tasks = new TaskList();
        try {
            File f = new File(this.filePath);
            Scanner myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                Todo todo = Todo.toTodo(data);
                Deadline deadline = Deadline.toDeadline(data);
                Event event = Event.toEvent(data);
                if (todo != null) {
                    tasks.addTask(todo);
                } else if (deadline != null) {
                    tasks.addTask(deadline);
                } else {
                    tasks.addTask(event);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
        }
        return tasks;
    }
}
