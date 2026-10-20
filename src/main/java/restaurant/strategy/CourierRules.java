package restaurant.strategy;
import java.util.List;
final class CourierRules {
    private CourierRules() {}
    static void requireCouriers(List<Courier> couriers) {
        if (couriers == null || couriers.isEmpty()) throw new IllegalArgumentException("At least one courier required");
    }
}
