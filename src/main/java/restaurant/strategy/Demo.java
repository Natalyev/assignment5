package restaurant.strategy;
import java.util.List;
import java.util.Map;
public class Demo {
    public static void main(String[] args) {
        List<FoodOrder> orders = List.of(
            new FoodOrder("Pasta", 1, 20, false),
            new FoodOrder("Soup", 2, 5, false),
            new FoodOrder("Pizza", 3, 15, true));
        List<Courier> couriers = List.of(
            new Courier("Alex", 2, 4), new Courier("Dana", 4, 0), new Courier("Mira", 3, 2));
        TrafficSource traffic = new SimulatedTrafficSource(Map.of("Alex", 3.0, "Dana", 1.0, "Mira", 1.2));
        QueueContext queue = new QueueContext(new FifoQueueStrategy());
        System.out.println("QUEUE - same input with three algorithms:");
        System.out.println("FIFO: " + names(queue.arrange(orders)));
        queue.setStrategy(new ShortestCookingQueueStrategy());
        System.out.println("Shortest: " + names(queue.arrange(orders)));
        queue.setStrategy(new UrgentFirstQueueStrategy());
        System.out.println("Urgent: " + names(queue.arrange(orders)));
        StrategySelector selector = new StrategySelector(traffic);
        queue.setStrategy(selector.selectQueue(orders));
        System.out.println("Automatic queue: " + names(queue.arrange(orders)));
        CourierContext delivery = new CourierContext(new NearestCourierStrategy());
        System.out.println("COURIER - same input with three algorithms:");
        System.out.println("Nearest: " + delivery.choose(couriers).name());
        delivery.setStrategy(new LeastBusyCourierStrategy());
        System.out.println("Least busy: " + delivery.choose(couriers).name());
        delivery.setStrategy(new TrafficAwareCourierStrategy(traffic));
        System.out.println("Traffic aware: " + delivery.choose(couriers).name());
        delivery.setStrategy(selector.selectCourier(couriers, true));
        System.out.println("Automatic courier at rush hour: " + delivery.choose(couriers).name());
    }
    private static String names(List<FoodOrder> orders) {
        return orders.stream().map(FoodOrder::id).toList().toString();
    }
}
