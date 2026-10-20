package restaurant.strategy;
import java.util.List;
public class StrategySelector {
    private final TrafficSource traffic;
    public StrategySelector(TrafficSource traffic) { this.traffic = traffic; }
    public QueueStrategy selectQueue(List<FoodOrder> orders) {
        if (orders == null) throw new IllegalArgumentException("orders required");
        if (orders.stream().anyMatch(FoodOrder::urgent)) return new UrgentFirstQueueStrategy();
        if (orders.size() >= 4) return new ShortestCookingQueueStrategy();
        return new FifoQueueStrategy();
    }
    public CourierStrategy selectCourier(List<Courier> couriers, boolean rushHour) {
        CourierRules.requireCouriers(couriers);
        if (rushHour) return new TrafficAwareCourierStrategy(traffic);
        if (couriers.stream().allMatch(c -> c.activeOrders() >= 3)) return new LeastBusyCourierStrategy();
        return new NearestCourierStrategy();
    }
}
