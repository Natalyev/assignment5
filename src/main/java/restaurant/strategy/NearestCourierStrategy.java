package restaurant.strategy;
import java.util.Comparator;
import java.util.List;
public class NearestCourierStrategy implements CourierStrategy {
    public Courier choose(List<Courier> couriers) {
        CourierRules.requireCouriers(couriers);
        return couriers.stream().min(Comparator.comparingDouble(Courier::distanceKm).thenComparing(Courier::name)).orElseThrow();
    }
}
