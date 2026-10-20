package restaurant.strategy;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class CourierStrategiesTest {
    private final List<Courier> couriers = List.of(new Courier("Alex", 2, 4), new Courier("Dana", 4, 0), new Courier("Mira", 3, 2));
    @Test void nearest() { assertEquals("Alex", new NearestCourierStrategy().choose(couriers).name()); }
    @Test void leastBusy() { assertEquals("Dana", new LeastBusyCourierStrategy().choose(couriers).name()); }
    @Test void trafficAwareUsesFakeData() {
        TrafficSource fake = name -> name.equals("Alex") ? 5.0 : 1.0;
        assertEquals("Mira", new TrafficAwareCourierStrategy(fake).choose(couriers).name());
    }
    @Test void switchesAtRuntime() {
        CourierContext context = new CourierContext(new NearestCourierStrategy());
        assertEquals("Alex", context.choose(couriers).name());
        context.setStrategy(new LeastBusyCourierStrategy());
        assertEquals("Dana", context.choose(couriers).name());
    }
    @Test void emptyListIsRejected() { assertThrows(IllegalArgumentException.class, () -> new NearestCourierStrategy().choose(List.of())); }
}
