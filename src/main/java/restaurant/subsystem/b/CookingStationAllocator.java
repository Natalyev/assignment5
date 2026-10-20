package restaurant.subsystem.b;

public class CookingStationAllocator {
    public String allocate(boolean forceFailure) {
        if (forceFailure) {
            throw new IllegalStateException("No cooking station is available");
        }
        return "STATION-1";
    }
}
