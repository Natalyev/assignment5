package restaurant;

import org.junit.jupiter.api.Test;
import restaurant.subsystem.a.InventoryChecker;
import restaurant.subsystem.a.Order;
import restaurant.subsystem.b.CookingStationAllocator;
import restaurant.subsystem.b.KitchenTask;
import restaurant.subsystem.b.QualityChecker;
import restaurant.subsystem.c.BillingModule;
import restaurant.subsystem.c.DeliveryDispatcher;
import restaurant.subsystem.c.FinalizationRecord;

import static org.junit.jupiter.api.Assertions.*;

class SubsystemRulesTest {

    @Test
    void orderControlsItsOwnTransitions() {
        Order order = new Order("1", "burger", 1);
        order.confirm();
        assertEquals(Order.Status.CONFIRMED, order.getStatus());
        order.cancel();
        assertEquals(Order.Status.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, order::cancel);
    }

    @Test
    void kitchenTaskControlsItsOwnTransitions() {
        KitchenTask task = new KitchenTask("1", "STATION-1");
        task.reserve();
        assertEquals(KitchenTask.Status.RESERVED, task.getStatus());
        task.release();
        assertEquals(KitchenTask.Status.RELEASED, task.getStatus());
        assertThrows(IllegalStateException.class, task::release);
    }

    @Test
    void finalizationControlsItsOwnTransitions() {
        FinalizationRecord complete = new FinalizationRecord("1", 10);
        complete.complete();
        assertEquals(FinalizationRecord.Status.COMPLETED, complete.getStatus());
        assertThrows(IllegalStateException.class, complete::complete);

        FinalizationRecord cancelled = new FinalizationRecord("2", 10);
        cancelled.cancel();
        assertEquals(FinalizationRecord.Status.CANCELLED, cancelled.getStatus());
        assertThrows(IllegalStateException.class, cancelled::cancel);
    }

    @Test
    void businessRulesCanRejectBadCases() {
        InventoryChecker inventory = new InventoryChecker();
        QualityChecker quality = new QualityChecker();
        CookingStationAllocator allocator = new CookingStationAllocator();
        BillingModule billing = new BillingModule();
        DeliveryDispatcher dispatcher = new DeliveryDispatcher();

        assertThrows(IllegalArgumentException.class, () -> inventory.check("", 1));
        assertThrows(IllegalArgumentException.class, () -> inventory.check("burger", 0));
        assertThrows(IllegalStateException.class, () -> inventory.check("soldout", 1));
        assertThrows(IllegalStateException.class, () -> quality.check("unsafe"));
        assertThrows(IllegalStateException.class, () -> allocator.allocate(true));
        assertThrows(IllegalArgumentException.class, () -> billing.calculateTotal(0));
        assertThrows(IllegalStateException.class, () -> dispatcher.dispatch(true));

        assertEquals("STATION-1", allocator.allocate(false));
        assertEquals(20.0, billing.calculateTotal(2));
        assertDoesNotThrow(() -> quality.check("burger"));
        assertDoesNotThrow(() -> dispatcher.dispatch(false));
    }
}
