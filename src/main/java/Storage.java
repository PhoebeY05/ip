import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Paths;

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

    public void saveToStorage(ArrayList<Task> tasks) {
        File f = new File(this.filePath);
        try (FileWriter fw = new FileWriter(f, false)) { // false = overwrite
            for (Task t : tasks) {
                fw.write(t + System.lineSeparator());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
