package restaurant.strategy;
import java.util.Comparator;
import java.util.List;
public class LeastBusyCourierStrategy implements CourierStrategy {
    public Courier choose(List<Courier> couriers) {
        CourierRules.requireCouriers(couriers);
        return couriers.stream().min(Comparator.comparingInt(Courier::activeOrders).thenComparingDouble(Courier::distanceKm)).orElseThrow();
    }
}
