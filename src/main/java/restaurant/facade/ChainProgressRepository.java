package restaurant.facade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

public class ChainProgressRepository {
    private final Path file;

    public ChainProgressRepository(Path baseDir) {
        this.file = baseDir.resolve("progress.csv");
        createStorage();
    }

    public void save(ChainProgress progress) {
        List<String> lines = readAll();
        String row = progress.getOrderId() + "," +
                progress.getStatus() + "," +
                clean(progress.getMessage());

        boolean replaced = false;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(progress.getOrderId() + ",")) {
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

    public Optional<ChainProgress> findById(String orderId) {
        return readAll().stream()
                .filter(line -> line.startsWith(orderId + ","))
                .map(this::fromRow)
                .findFirst();
    }

    private ChainProgress fromRow(String row) {
        String[] parts = row.split(",", -1);
        return new ChainProgress(
                parts[0],
                ChainProgress.Status.valueOf(parts[1]),
                parts.length > 2 ? parts[2] : ""
        );
    }

    private String clean(String value) {
        return value == null ? "" : value.replace(",", " ");
    }

    private void createStorage() {
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create progress storage", e);
        }
    }

    private List<String> readAll() {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read progress storage", e);
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
            throw new IllegalStateException("Cannot write progress storage", e);
        }
    }
}
