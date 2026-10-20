package restaurant.strategy;
import java.util.Comparator;
import java.util.List;
public class TrafficAwareCourierStrategy implements CourierStrategy {
    private final TrafficSource traffic;
    public TrafficAwareCourierStrategy(TrafficSource traffic) {
        if (traffic == null) throw new IllegalArgumentException("traffic required");
        this.traffic = traffic;
    }
    public Courier choose(List<Courier> couriers) {
        CourierRules.requireCouriers(couriers);
        return couriers.stream().min(Comparator.comparingDouble(c -> travelScore(c))).orElseThrow();
    }
    private double travelScore(Courier c) {
        double multiplier = traffic.multiplierFor(c.name());
        if (multiplier <= 0) throw new IllegalArgumentException("Invalid traffic multiplier");
        return c.distanceKm() * multiplier;
    }
}
