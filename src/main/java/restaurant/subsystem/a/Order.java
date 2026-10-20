package restaurant.subsystem.a;

public class Order {
    public enum Status {
        NEW, CONFIRMED, CANCELLED
    }

    private final String id;
    private final String item;
    private final int quantity;
    private Status status;

    public Order(String id, String item, int quantity) {
        this(id, item, quantity, Status.NEW);
    }

    public Order(String id, String item, int quantity, Status status) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
    }

    public void confirm() {
        if (status != Status.NEW) {
            throw new IllegalStateException("Only NEW order can be confirmed");
        }
        status = Status.CONFIRMED;
    }

    public void cancel() {
        if (status == Status.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        status = Status.CANCELLED;
    }

    public String getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public Status getStatus() {
        return status;
    }
}
