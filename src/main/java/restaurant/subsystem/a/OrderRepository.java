package restaurant.subsystem.a;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

public class OrderRepository {
    private final Path file;

    public OrderRepository(Path baseDir) {
        this.file = baseDir.resolve("orders.csv");
        createStorage();
    }

    public void save(Order order) {
        List<String> lines = readAll();
        String row = toRow(order);
        boolean replaced = false;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(order.getId() + ",")) {
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

    public Optional<Order> findById(String id) {
        return readAll().stream()
                .filter(line -> line.startsWith(id + ","))
                .map(this::fromRow)
                .findFirst();
    }

    public List<Order> findAll() {
        return readAll().stream().map(this::fromRow).toList();
    }

    private String toRow(Order order) {
        return order.getId() + "," +
                clean(order.getItem()) + "," +
                order.getQuantity() + "," +
                order.getStatus();
    }

    private Order fromRow(String row) {
        String[] parts = row.split(",", -1);
        return new Order(
                parts[0],
                parts[1],
                Integer.parseInt(parts[2]),
                Order.Status.valueOf(parts[3])
        );
    }

    private String clean(String value) {
        return value.replace(",", " ");
    }

    private void createStorage() {
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create order storage", e);
        }
    }

    private List<String> readAll() {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read order storage", e);
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
            throw new IllegalStateException("Cannot write order storage", e);
        }
    }
}
