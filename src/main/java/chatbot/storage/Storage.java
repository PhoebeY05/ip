package chatbot.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import chatbot.exception.ChatBotException;
import chatbot.task.Deadline;
import chatbot.task.Event;
import chatbot.task.Task;
import chatbot.task.TaskList;
import chatbot.task.Todo;

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
            // Create parent directories if they do not exist
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create parent directories: " + e.getMessage());
        }

        try {
            // Create the file if it does not exist
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
        File file = new File(this.filePath);

        // Overwrite file content on each save instead of appending
        try (FileWriter writer = new FileWriter(file, false)) {
            for (Task task : tasks.getTasks()) {
                if (task != null) {
                    writer.write(task + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks to storage: " + e.getMessage());
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
            File file = new File(this.filePath);
            try (Scanner reader = new Scanner(file)) { // Auto-close scanner
                while (reader.hasNextLine()) {
                    String line = reader.nextLine().trim();

                    if (line.isEmpty()) {
                        continue; // Skip empty lines
                    }

                    // Determine task type based on prefix
                    if (line.startsWith("[T]")) {
                        tasks.add(Todo.convertToTodo(line));
                    } else if (line.startsWith("[D]")) {
                        tasks.add(Deadline.convertToDeadline(line));
                    } else if (line.startsWith("[E]")) {
                        tasks.add(Event.convertToEvent(line));
                    } else {
                        throw new ChatBotException("OOPS!! Data file has unknown line: " + line);
                    }
                }
            }
        } catch (IOException | ChatBotException e) {
            throw new ChatBotException("Failed to load tasks: " + e.getMessage());
        }

        return tasks;
    }
}