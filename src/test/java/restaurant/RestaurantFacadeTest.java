package restaurant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import restaurant.facade.ChainProgress;
import restaurant.facade.RestaurantOperationException;
import restaurant.subsystem.a.Order;
import restaurant.subsystem.b.KitchenTask;
import restaurant.subsystem.c.FinalizationRecord;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantFacadeTest {

    @TempDir
    Path tempDir;

    @Test
    void successfulChainCompletesAllSubsystems() {
        RestaurantScenario s = new RestaurantScenario(tempDir);

        String id = s.aConfirmedOrderExists("burger", 2);
        s.whenTheChainContinues(id);

        assertEquals(Order.Status.CONFIRMED, s.orders.find(id).get().getStatus());
        assertEquals(KitchenTask.Status.RESERVED, s.kitchen.find(id).get().getStatus());
        assertEquals(FinalizationRecord.Status.COMPLETED, s.finalization.find(id).get().getStatus());
        assertEquals(ChainProgress.Status.COMPLETED, s.facade.getProgress(id).getStatus());
    }

    @Test
    void bFailureCompensatesOnlyA() {
        RestaurantScenario s = new RestaurantScenario(tempDir);

        String id = s.aConfirmedOrderExists("burger", 1);

        assertThrows(
                RestaurantOperationException.class,
                () -> s.whenBFailureHappens(id)
        );

        assertEquals(Order.Status.CANCELLED, s.orders.find(id).get().getStatus());
        assertTrue(s.kitchen.find(id).isEmpty());
        assertTrue(s.finalization.find(id).isEmpty());
        assertEquals(ChainProgress.Status.COMPENSATED, s.facade.getProgress(id).getStatus());
    }

    @Test
    void cFailureCompensatesBThenA() {
        RestaurantScenario s = new RestaurantScenario(tempDir);

        String id = s.aConfirmedOrderExists("pizza", 1);

        assertThrows(
                RestaurantOperationException.class,
                () -> s.whenCFailureHappens(id)
        );

        assertEquals(Order.Status.CANCELLED, s.orders.find(id).get().getStatus());
        assertEquals(KitchenTask.Status.RELEASED, s.kitchen.find(id).get().getStatus());
        assertEquals(FinalizationRecord.Status.CANCELLED, s.finalization.find(id).get().getStatus());
        assertEquals(ChainProgress.Status.COMPENSATED, s.facade.getProgress(id).getStatus());
    }

    @Test
    void invalidInputFailsBeforeAnythingCommits() {
        RestaurantScenario s = new RestaurantScenario(tempDir);

        assertThrows(
                RestaurantOperationException.class,
                () -> s.facade.beginOrder("", 1)
        );

        assertTrue(s.orders.find("missing").isEmpty());
    }
}
