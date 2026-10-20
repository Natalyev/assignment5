package restaurant.subsystem.b;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

public class KitchenRepository {
    private final Path file;

    public KitchenRepository(Path baseDir) {
        this.file = baseDir.resolve("kitchen.csv");
        createStorage();
    }

    public void save(KitchenTask task) {
        List<String> lines = readAll();
        String row = task.getOrderId() + "," +
                task.getStation() + "," +
                task.getStatus();

        boolean replaced = false;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(task.getOrderId() + ",")) {
                lines.set(i, row);
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            lines.add(row);
        }

        writeAll(lines);
    }

    public Optional<KitchenTask> findById(String orderId) {
        return readAll().stream()
                .filter(line -> line.startsWith(orderId + ","))
                .map(this::fromRow)
                .findFirst();
    }

    private KitchenTask fromRow(String row) {
        String[] parts = row.split(",", -1);
        return new KitchenTask(
                parts[0],
                parts[1],
                KitchenTask.Status.valueOf(parts[2])
        );
    }

    private void createStorage() {
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create kitchen storage", e);
        }
    }

    private List<String> readAll() {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read kitchen storage", e);
        }
    }

    private void writeAll(List<String> lines) {
        try {
            Files.write(
                    file,
                    lines,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write kitchen storage", e);
        }
    }
}
