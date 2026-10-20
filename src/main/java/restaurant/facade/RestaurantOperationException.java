package restaurant.facade;

public class RestaurantOperationException extends RuntimeException {
    public RestaurantOperationException(String message) {
        super(message);
    }

    public RestaurantOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
