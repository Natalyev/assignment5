package restaurant.strategy;
public record FoodOrder(String id, int arrival, int cookingMinutes, boolean urgent) {
    public FoodOrder {
        if (id == null || id.isBlank() || arrival < 0 || cookingMinutes <= 0) throw new IllegalArgumentException("Invalid food order");
    }
}
