package restaurant.subsystem.a;

public class InventoryChecker {
    public void check(String item, int quantity) {
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (item.equalsIgnoreCase("soldout")) {
            throw new IllegalStateException("Item is out of stock");
        }
    }
}
