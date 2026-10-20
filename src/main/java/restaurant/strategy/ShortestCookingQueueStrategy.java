package restaurant.strategy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class ShortestCookingQueueStrategy implements QueueStrategy {
    public List<FoodOrder> arrange(List<FoodOrder> orders) {
        if (orders == null) throw new IllegalArgumentException("orders required");
        List<FoodOrder> result = new ArrayList<>(orders);
        result.sort(Comparator.comparingInt(FoodOrder::cookingMinutes).thenComparingInt(FoodOrder::arrival));
        return result;
    }
}
