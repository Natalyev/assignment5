package restaurant.facade;

public class ChainProgress {
    public enum Status {
        PENDING_B, PENDING_C, COMPLETED, COMPENSATED
    }

    private final String orderId;
    private Status status;
    private String message;

    public ChainProgress(String orderId, Status status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public void moveTo(Status newStatus, String newMessage) {
        status = newStatus;
        message = newMessage;
    }

    public String getOrderId() {
        return orderId;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
