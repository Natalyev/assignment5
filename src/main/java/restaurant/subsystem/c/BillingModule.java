package restaurant.subsystem.c;

public class BillingModule {
    public double calculateTotal(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return quantity * 10.0;
    }
}
