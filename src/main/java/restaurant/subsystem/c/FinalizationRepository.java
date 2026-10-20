package restaurant.subsystem.c;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

public class FinalizationRepository {
    private final Path file;

    public FinalizationRepository(Path baseDir) {
        this.file = baseDir.resolve("finalization.csv");
        createStorage();
    }

    public void save(FinalizationRecord record) {
        List<String> lines = readAll();
        String row = record.getOrderId() + "," +
                record.getTotal() + "," +
                record.getStatus();

        boolean replaced = false;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(record.getOrderId() + ",")) {
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

    public Optional<FinalizationRecord> findById(String orderId) {
        return readAll().stream()
                .filter(line -> line.startsWith(orderId + ","))
                .map(this::fromRow)
                .findFirst();
    }

    private FinalizationRecord fromRow(String row) {
        String[] parts = row.split(",", -1);
        return new FinalizationRecord(
                parts[0],
                Double.parseDouble(parts[1]),
                FinalizationRecord.Status.valueOf(parts[2])
        );
    }

    private void createStorage() {
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create finalization storage", e);
        }
    }

    private List<String> readAll() {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read finalization storage", e);
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
            throw new IllegalStateException("Cannot write finalization storage", e);
        }
    }
}
