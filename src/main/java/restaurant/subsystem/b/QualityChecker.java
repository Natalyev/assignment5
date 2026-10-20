package restaurant.subsystem.b;

public class QualityChecker {
    public void check(String item) {
        if (item.equalsIgnoreCase("unsafe")) {
            throw new IllegalStateException("Item failed kitchen quality check");
        }
    }
}
