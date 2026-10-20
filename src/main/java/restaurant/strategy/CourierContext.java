package restaurant.strategy;
import java.util.List;
public class CourierContext {
    private CourierStrategy strategy;
    public CourierContext(CourierStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(CourierStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("strategy required");
        this.strategy = strategy;
    }
    public Courier choose(List<Courier> couriers) { return strategy.choose(couriers); }
}
