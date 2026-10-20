package restaurant.subsystem.b;

public class KitchenTask {
    public enum Status {
        NEW, RESERVED, RELEASED
    }

    private final String orderId;
    private final String station;
    private Status status;

    public KitchenTask(String orderId, String station) {
        this(orderId, station, Status.NEW);
    }

    public KitchenTask(String orderId, String station, Status status) {
        this.orderId = orderId;
        this.station = station;
        this.status = status;
    }

    public void reserve() {
        if (status != Status.NEW) {
            throw new IllegalStateException("Only NEW kitchen task can be reserved");
        }
        status = Status.RESERVED;
    }

    public void release() {
        if (status != Status.RESERVED) {
            throw new IllegalStateException("Only RESERVED task can be released");
        }
        status = Status.RELEASED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStation() {
        return station;
    }

    public Status getStatus() {
        return status;
    }
}
