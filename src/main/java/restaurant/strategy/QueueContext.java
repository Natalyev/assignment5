package restaurant.strategy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class QueueContext {
    private QueueStrategy strategy;
    public QueueContext(QueueStrategy strategy) { setStrategy(strategy); }
    public void setStrategy(QueueStrategy strategy) { if (strategy == null) throw new IllegalArgumentException("strategy required"); this.strategy = strategy; }
    public List<FoodOrder> arrange(List<FoodOrder> orders) {
        // BAD FIRST VERSION: all algorithms and concrete-type checks are in the context.
        if (orders == null) throw new IllegalArgumentException("orders required");
        List<FoodOrder> copy = new ArrayList<>(orders);
        if (strategy instanceof FifoQueueStrategy) {
            copy.sort(Comparator.comparingInt(FoodOrder::arrival));
        } else if (strategy instanceof ShortestCookingQueueStrategy) {
            copy.sort(Comparator.comparingInt(FoodOrder::cookingMinutes).thenComparingInt(FoodOrder::arrival));
        } else if (strategy instanceof UrgentFirstQueueStrategy) {
            copy.sort(Comparator.comparing(FoodOrder::urgent).reversed().thenComparingInt(FoodOrder::arrival));
        } else throw new IllegalArgumentException("Unknown strategy");
        return copy;
    }
}
