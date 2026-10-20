package restaurant.strategy;
import java.util.Map;
public class SimulatedTrafficSource implements TrafficSource {
    private final Map<String,Double> values;
    public SimulatedTrafficSource(Map<String,Double> values) { this.values = Map.copyOf(values); }
    public double multiplierFor(String courierName) { return values.getOrDefault(courierName, 1.0); }
}
