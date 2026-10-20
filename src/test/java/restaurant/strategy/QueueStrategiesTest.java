package restaurant.strategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
class QueueStrategiesTest {
    private final List<FoodOrder> orders = List.of(
        new FoodOrder("Pasta", 1, 20, false), new FoodOrder("Soup", 2, 5, false), new FoodOrder("Pizza", 3, 15, true));
    @Test void fifo() { assertEquals("Pasta", new QueueContext(new FifoQueueStrategy()).arrange(orders).get(0).id()); }
    @Test void shortest() { assertEquals("Soup", new QueueContext(new ShortestCookingQueueStrategy()).arrange(orders).get(0).id()); }
    @Test void urgent() { assertEquals("Pizza", new QueueContext(new UrgentFirstQueueStrategy()).arrange(orders).get(0).id()); }
    @Test void switchesDuringRuntime() {
        QueueContext context = new QueueContext(new FifoQueueStrategy());
        assertEquals("Pasta", context.arrange(orders).get(0).id());
        context.setStrategy(new UrgentFirstQueueStrategy());
        assertEquals("Pizza", context.arrange(orders).get(0).id());
    }
    static Stream<QueueStrategy> allStrategies() {
        return Stream.of(new FifoQueueStrategy(), new ShortestCookingQueueStrategy(), new UrgentFirstQueueStrategy());
    }
    @ParameterizedTest @MethodSource("allStrategies")
    void everyStrategyKeepsAllOrders(QueueStrategy strategy) {
        List<FoodOrder> actual = new QueueContext(strategy).arrange(ordersForTest());
        assertEquals(3, actual.size());
        assertTrue(actual.containsAll(ordersForTest()));
    }
    private static List<FoodOrder> ordersForTest() {
        return List.of(new FoodOrder("A", 1, 3, false), new FoodOrder("B", 2, 1, true), new FoodOrder("C", 3, 5, false));
    }
}
