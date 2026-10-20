package restaurant.strategy;
public record Courier(String name, double distanceKm, int activeOrders) {
    public Courier {
        if (name == null || name.isBlank() || distanceKm < 0 || activeOrders < 0) throw new IllegalArgumentException("Invalid courier");
    }
}
