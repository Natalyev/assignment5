package restaurant.facade;

import restaurant.subsystem.a.Order;
import restaurant.subsystem.a.OrderQueueService;
import restaurant.subsystem.b.KitchenService;
import restaurant.subsystem.c.FinalizationService;

public class RestaurantFacade {
    private final OrderQueueService orderService;
    private final KitchenService kitchenService;
    private final FinalizationService finalizationService;
    private final ChainProgressRepository progressRepository;

    public RestaurantFacade(
            OrderQueueService orderService,
            KitchenService kitchenService,
            FinalizationService finalizationService,
            ChainProgressRepository progressRepository
    ) {
        this.orderService = orderService;
        this.kitchenService = kitchenService;
        this.finalizationService = finalizationService;
        this.progressRepository = progressRepository;
    }

    public String placeOrder(
            String item,
            int quantity,
            boolean failAtB,
            boolean failAtC
    ) {
        String orderId = beginOrder(item, quantity);
        continueOrder(orderId, failAtB, failAtC);
        return orderId;
    }

    public String beginOrder(String item, int quantity) {
        String orderId = "ORD-" + System.currentTimeMillis();

        try {
            orderService.createAndConfirm(orderId, item, quantity);

            ChainProgress progress = new ChainProgress(
                    orderId,
                    ChainProgress.Status.PENDING_B,
                    "Subsystem A committed"
            );
            progressRepository.save(progress);

            return orderId;
        } catch (RuntimeException e) {
            throw new RestaurantOperationException(
                    "Order could not start: " + e.getMessage(),
                    e
            );
        }
    }

    public void continueOrder(
            String orderId,
            boolean failAtB,
            boolean failAtC
    ) {
        ChainProgress progress = loadProgress(orderId);
        Order order = orderService.find(orderId)
                .orElseThrow(() -> new RestaurantOperationException("Order not found"));

        if (progress.getStatus() == ChainProgress.Status.PENDING_B) {
            try {
                kitchenService.reserve(
                        orderId,
                        order.getItem(),
                        failAtB
                );

                progress.moveTo(
                        ChainProgress.Status.PENDING_C,
                        "Subsystem A and B committed"
                );
                progressRepository.save(progress);
            } catch (RuntimeException e) {
                compensateFromPersistedState(
                        orderId,
                        "B failed: " + e.getMessage()
                );
                throw new RestaurantOperationException(
                        "Subsystem B failed. A was compensated.",
                        e
                );
            }
        }

        progress = loadProgress(orderId);

        if (progress.getStatus() == ChainProgress.Status.PENDING_C) {
            try {
                finalizationService.finalizeOrder(
                        orderId,
                        order.getQuantity(),
                        failAtC
                );

                progress.moveTo(
                        ChainProgress.Status.COMPLETED,
                        "A, B and C completed"
                );
                progressRepository.save(progress);
            } catch (RuntimeException e) {
                compensateFromPersistedState(
                        orderId,
                        "C failed: " + e.getMessage()
                );
                throw new RestaurantOperationException(
                        "Subsystem C failed. B and A were compensated.",
                        e
                );
            }
        }
    }

    public void compensateFromPersistedState(
            String orderId,
            String reason
    ) {
        ChainProgress progress = loadProgress(orderId);

        if (progress.getStatus() == ChainProgress.Status.PENDING_C) {
            finalizationService.cancelIfPresent(orderId);
            kitchenService.release(orderId);
            orderService.cancel(orderId);
        } else if (progress.getStatus() == ChainProgress.Status.PENDING_B) {
            orderService.cancel(orderId);
        }

        progress.moveTo(
                ChainProgress.Status.COMPENSATED,
                reason
        );
        progressRepository.save(progress);
    }

    public ChainProgress getProgress(String orderId) {
        return loadProgress(orderId);
    }

    private ChainProgress loadProgress(String orderId) {
        return progressRepository.findById(orderId)
                .orElseThrow(() ->
                        new RestaurantOperationException(
                                "Progress not found for " + orderId
                        )
                );
    }
}
