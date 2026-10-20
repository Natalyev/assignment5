package restaurant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import restaurant.facade.ChainProgress;
import restaurant.subsystem.a.Order;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AdminFacadeTest {

    @TempDir
    Path tempDir;

    @Test
    void adminCanInspectCompletedOrder() {
        RestaurantScenario s = new RestaurantScenario(tempDir);

        String id = s.facade.placeOrder("burger", 1, false, false);
        String text = s.admin.inspect(id);

        assertTrue(text.contains("Order=CONFIRMED"));
        assertTrue(text.contains("Kitchen=RESERVED"));
        assertTrue(text.contains("Finalization=COMPLETED"));
        assertTrue(text.contains("Progress=COMPLETED"));
    }

    @Test
    void adminCanManuallyCompensatePendingB() {
        RestaurantScenario s = new RestaurantScenario(tempDir);

        String id = s.aConfirmedOrderExists("burger", 1);
        s.admin.manualCompensate(id);

        assertEquals(Order.Status.CANCELLED, s.orders.find(id).get().getStatus());
        assertEquals(ChainProgress.Status.COMPENSATED, s.facade.getProgress(id).getStatus());
    }
}
