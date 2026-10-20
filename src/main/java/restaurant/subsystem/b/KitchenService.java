package restaurant.subsystem.b;

import java.nio.file.Path;
import java.util.Optional;

public class KitchenService {
    private final CookingStationAllocator allocator;
    private final QualityChecker qualityChecker;
    private final KitchenRepository repository;

    public KitchenService(
            CookingStationAllocator allocator,
            QualityChecker qualityChecker,
            KitchenRepository repository
    ) {
        this.allocator = allocator;
        this.qualityChecker = qualityChecker;
        this.repository = repository;
    }

    public KitchenTask reserve(String orderId, String item, boolean forceFailure) {
        qualityChecker.check(item);
        String station = allocator.allocate(forceFailure);

        KitchenTask task = new KitchenTask(orderId, station);
        task.reserve();
        repository.save(task);
        return task;
    }

    public void release(String orderId) {
        KitchenTask task = repository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Kitchen task not found: " + orderId));

        if (task.getStatus() == KitchenTask.Status.RESERVED) {
            task.release();
            repository.save(task);
        }
    }

    public Optional<KitchenTask> find(String orderId) {
        return repository.findById(orderId);
    }

    public static void main(String[] args) {
        Path path = Path.of("data-b-demo");
        KitchenService service = new KitchenService(
                new CookingStationAllocator(),
                new QualityChecker(),
                new KitchenRepository(path)
        );

        service.reserve("B-DEMO", "burger", false);
        System.out.println(service.find("B-DEMO").get().getStatus());
    }
}
