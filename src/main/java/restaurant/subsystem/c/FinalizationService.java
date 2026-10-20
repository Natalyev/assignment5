package restaurant.subsystem.c;

import java.nio.file.Path;
import java.util.Optional;

public class FinalizationService {
    private final BillingModule billingModule;
    private final DeliveryDispatcher dispatcher;
    private final FinalizationRepository repository;

    public FinalizationService(
            BillingModule billingModule,
            DeliveryDispatcher dispatcher,
            FinalizationRepository repository
    ) {
        this.billingModule = billingModule;
        this.dispatcher = dispatcher;
        this.repository = repository;
    }

    public FinalizationRecord finalizeOrder(
            String orderId,
            int quantity,
            boolean forceFailure
    ) {
        double total = billingModule.calculateTotal(quantity);

        FinalizationRecord record = new FinalizationRecord(orderId, total);
        repository.save(record);

        dispatcher.dispatch(forceFailure);

        record.complete();
        repository.save(record);
        return record;
    }

    public void cancelIfPresent(String orderId) {
        Optional<FinalizationRecord> optional = repository.findById(orderId);

        if (optional.isPresent()) {
            FinalizationRecord record = optional.get();

            if (record.getStatus() == FinalizationRecord.Status.NEW) {
                record.cancel();
                repository.save(record);
            }
        }
    }

    public Optional<FinalizationRecord> find(String orderId) {
        return repository.findById(orderId);
    }

    public static void main(String[] args) {
        Path path = Path.of("data-c-demo");
        FinalizationService service = new FinalizationService(
                new BillingModule(),
                new DeliveryDispatcher(),
                new FinalizationRepository(path)
        );

        service.finalizeOrder("C-DEMO", 2, false);
        System.out.println(service.find("C-DEMO").get().getStatus());
    }
}
