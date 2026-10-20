package restaurant.strategy;
import java.util.List;
public interface QueueStrategy { List<FoodOrder> arrange(List<FoodOrder> orders); }
