package restaurant.subsystem.c;

public class DeliveryDispatcher {
    public void dispatch(boolean forceFailure) {
        if (forceFailure) {
            throw new IllegalStateException("Delivery dispatcher is unavailable");
        }
    }
}
