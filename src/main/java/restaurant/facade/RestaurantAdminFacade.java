package restaurant.facade;

import restaurant.subsystem.a.OrderQueueService;
import restaurant.subsystem.b.KitchenService;
import restaurant.subsystem.c.FinalizationService;

public class RestaurantAdminFacade {
    private final RestaurantFacade workflowFacade;
    private final OrderQueueService orderService;
    private final KitchenService kitchenService;
    private final FinalizationService finalizationService;

    public RestaurantAdminFacade(
            RestaurantFacade workflowFacade,
            OrderQueueService orderService,
            KitchenService kitchenService,
            FinalizationService finalizationService
    ) {
        this.workflowFacade = workflowFacade;
        this.orderService = orderService;
        this.kitchenService = kitchenService;
        this.finalizationService = finalizationService;
    }

    public String inspect(String orderId) {
        String orderStatus = orderService.find(orderId)
                .map(order -> order.getStatus().name())
                .orElse("NONE");

        String kitchenStatus = kitchenService.find(orderId)
                .map(task -> task.getStatus().name())
                .orElse("NONE");

        String finalStatus = finalizationService.find(orderId)
                .map(record -> record.getStatus().name())
                .orElse("NONE");

        String progressStatus = workflowFacade.getProgress(orderId)
                .getStatus()
                .name();

        return "Order=" + orderStatus +
                ", Kitchen=" + kitchenStatus +
                ", Finalization=" + finalStatus +
                ", Progress=" + progressStatus;
    }

    public void manualCompensate(String orderId) {
        workflowFacade.compensateFromPersistedState(
                orderId,
                "Manual compensation by admin"
        );
    }
}
