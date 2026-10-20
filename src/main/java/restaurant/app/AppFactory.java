package restaurant.app;

import restaurant.facade.ChainProgressRepository;
import restaurant.facade.RestaurantAdminFacade;
import restaurant.facade.RestaurantFacade;
import restaurant.subsystem.a.InventoryChecker;
import restaurant.subsystem.a.OrderQueueService;
import restaurant.subsystem.a.OrderRepository;
import restaurant.subsystem.b.CookingStationAllocator;
import restaurant.subsystem.b.KitchenRepository;
import restaurant.subsystem.b.KitchenService;
import restaurant.subsystem.b.QualityChecker;
import restaurant.subsystem.c.BillingModule;
import restaurant.subsystem.c.DeliveryDispatcher;
import restaurant.subsystem.c.FinalizationRepository;
import restaurant.subsystem.c.FinalizationService;

import java.nio.file.Path;

public class AppFactory {
    private final RestaurantFacade restaurantFacade;
    private final RestaurantAdminFacade adminFacade;

    public AppFactory(Path dataDir) {
        OrderQueueService orderService = new OrderQueueService(
                new InventoryChecker(),
                new OrderRepository(dataDir)
        );

        KitchenService kitchenService = new KitchenService(
                new CookingStationAllocator(),
                new QualityChecker(),
                new KitchenRepository(dataDir)
        );

        FinalizationService finalizationService = new FinalizationService(
                new BillingModule(),
                new DeliveryDispatcher(),
                new FinalizationRepository(dataDir)
        );

        restaurantFacade = new RestaurantFacade(
                orderService,
                kitchenService,
                finalizationService,
                new ChainProgressRepository(dataDir)
        );

        adminFacade = new RestaurantAdminFacade(
                restaurantFacade,
                orderService,
                kitchenService,
                finalizationService
        );
    }

    public RestaurantFacade restaurantFacade() {
        return restaurantFacade;
    }

    public RestaurantAdminFacade adminFacade() {
        return adminFacade;
    }
}
