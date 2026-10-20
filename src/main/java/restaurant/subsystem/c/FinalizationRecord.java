package restaurant.subsystem.c;

public class FinalizationRecord {
    public enum Status {
        NEW, COMPLETED, CANCELLED
    }

    private final String orderId;
    private final double total;
    private Status status;

    public FinalizationRecord(String orderId, double total) {
        this(orderId, total, Status.NEW);
    }

    public FinalizationRecord(String orderId, double total, Status status) {
        this.orderId = orderId;
        this.total = total;
        this.status = status;
    }

    public void complete() {
        if (status != Status.NEW) {
            throw new IllegalStateException("Only NEW finalization can be completed");
        }
        status = Status.COMPLETED;
    }

    public void cancel() {
        if (status != Status.NEW) {
            throw new IllegalStateException("Only NEW finalization can be cancelled");
        }
        status = Status.CANCELLED;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getTotal() {
        return total;
    }

    public Status getStatus() {
        return status;
    }
}
