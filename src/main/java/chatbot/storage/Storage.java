package chatbot.storage;

import chatbot.exception.ChatBotException;
import chatbot.task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles the saving and loading of tasks to and from hard drive.
 * Tasks are stored in a text file
 */
public class Storage {

    private final String filePath;

    /**
     * Constructs a Storage object with the given file path.
     * Ensures that the parent directories and file exist, creating them if necessary.
     *
     * @param filePath Path to the storage file.
     */
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

    /**
     * Saves the current list of tasks to storage by overwriting the existing file.
     * Each task is written on a separate line in string format.
     *
     * @param tasks The {@link TaskList} containing tasks to be saved.
     */
    public void saveToStorage(TaskList tasks) {
        File f = new File(this.filePath);
        try (FileWriter fw = new FileWriter(f, false)) { // false = overwrite
            for (Task t : tasks.getTasks()) {
                if (t != null) {
                    fw.write(t + System.lineSeparator());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads tasks from the storage file and reconstructs them into memory.
     * Recognizes tasks based on their prefixes:
     * <ul>
     *     <li>[T] → {@link Todo}</li>
     *     <li>[D] → {@link Deadline}</li>
     *     <li>[E] → {@link Event}</li>
     * </ul>
     *
     * @return A list of reconstructed {@link Task} objects.
     * @throws ChatBotException If the file contains invalid or unrecognized task formats,
     *                          or if an I/O error occurs.
     */
    public ArrayList<Task> load() throws ChatBotException {
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            File f = new File(this.filePath);
            Scanner myReader = new Scanner(f);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
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
            myReader.close();
        } catch (Exception e) {
            throw new ChatBotException(e.getMessage());
        }
        return tasks;
    }
}
