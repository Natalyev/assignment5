package restaurant.strategy;
import java.util.List;
public class QueueContext {
    private QueueStrategy strategy;
    public QueueContext(QueueStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(QueueStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("strategy required");
        this.strategy = strategy;
    }
    public List<FoodOrder> arrange(List<FoodOrder> orders) {
        return strategy.arrange(orders);
    }
}
