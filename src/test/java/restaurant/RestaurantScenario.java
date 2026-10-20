package restaurant;

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

class RestaurantScenario {
    final Path dir;
    final OrderQueueService orders;
    final KitchenService kitchen;
    final FinalizationService finalization;
    final RestaurantFacade facade;
    final RestaurantAdminFacade admin;

    RestaurantScenario(Path dir) {
        this.dir = dir;

        orders = new OrderQueueService(
                new InventoryChecker(),
                new OrderRepository(dir)
        );

        kitchen = new KitchenService(
                new CookingStationAllocator(),
                new QualityChecker(),
                new KitchenRepository(dir)
        );

        finalization = new FinalizationService(
                new BillingModule(),
                new DeliveryDispatcher(),
                new FinalizationRepository(dir)
        );

        facade = new RestaurantFacade(
                orders,
                kitchen,
                finalization,
                new ChainProgressRepository(dir)
        );

        admin = new RestaurantAdminFacade(
                facade,
                orders,
                kitchen,
                finalization
        );
    }

    String aConfirmedOrderExists(String item, int quantity) {
        return facade.beginOrder(item, quantity);
    }

    void whenTheChainContinues(String orderId) {
        facade.continueOrder(orderId, false, false);
    }

    void whenBFailureHappens(String orderId) {
        facade.continueOrder(orderId, true, false);
    }

    void whenCFailureHappens(String orderId) {
        facade.continueOrder(orderId, false, true);
    }
}
