package restaurant.strategy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class UrgentFirstQueueStrategy implements QueueStrategy {
    public List<FoodOrder> arrange(List<FoodOrder> orders) {
        if (orders == null) throw new IllegalArgumentException("orders required");
        List<FoodOrder> result = new ArrayList<>(orders);
        result.sort(Comparator.comparing(FoodOrder::urgent).reversed().thenComparingInt(FoodOrder::arrival));
        return result;
    }
}
