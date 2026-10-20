package restaurant.subsystem.a;

import java.nio.file.Path;
import java.util.Optional;

public class OrderQueueService {
    private final InventoryChecker inventoryChecker;
    private final OrderRepository repository;

    public OrderQueueService(InventoryChecker inventoryChecker, OrderRepository repository) {
        this.inventoryChecker = inventoryChecker;
        this.repository = repository;
    }

    public Order createAndConfirm(String id, String item, int quantity) {
        inventoryChecker.check(item, quantity);

        Order order = new Order(id, item, quantity);
        repository.save(order);

        order.confirm();
        repository.save(order);
        return order;
    }

    public void cancel(String id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + id));

        if (order.getStatus() != Order.Status.CANCELLED) {
            order.cancel();
            repository.save(order);
        }
    }

    public Optional<Order> find(String id) {
        return repository.findById(id);
    }

    public static void main(String[] args) {
        Path path = Path.of("data-a-demo");
        OrderQueueService service = new OrderQueueService(
                new InventoryChecker(),
                new OrderRepository(path)
        );

        service.createAndConfirm("A-DEMO", "burger", 1);
        System.out.println(service.find("A-DEMO").get().getStatus());
    }
}
