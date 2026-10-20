package restaurant.strategy;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class StrategySelectorTest {
    private final StrategySelector selector = new StrategySelector(name -> 1.0);
    private FoodOrder order(String id, int arrival, boolean urgent) { return new FoodOrder(id, arrival, 10, urgent); }
    @Test void urgentOrdersUseUrgentStrategy() {
        assertInstanceOf(UrgentFirstQueueStrategy.class, selector.selectQueue(List.of(order("A",1,true))));
    }
    @Test void largeQueueUsesShortestStrategy() {
        assertInstanceOf(ShortestCookingQueueStrategy.class, selector.selectQueue(List.of(order("A",1,false),order("B",2,false),order("C",3,false),order("D",4,false))));
    }
    @Test void smallQueueUsesFifo() {
        assertInstanceOf(FifoQueueStrategy.class, selector.selectQueue(List.of(order("A",1,false))));
    }
    @Test void rushHourUsesTraffic() {
        assertInstanceOf(TrafficAwareCourierStrategy.class, selector.selectCourier(List.of(new Courier("A",1,0)), true));
    }
    @Test void allBusyUsesLeastBusy() {
        assertInstanceOf(LeastBusyCourierStrategy.class, selector.selectCourier(List.of(new Courier("A",1,3),new Courier("B",2,4)), false));
    }
    @Test void normalCourierChoiceUsesNearest() {
        assertInstanceOf(NearestCourierStrategy.class, selector.selectCourier(List.of(new Courier("A",1,0)), false));
    }
}
