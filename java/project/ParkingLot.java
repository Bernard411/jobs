import java.util.HashMap;
import java.util.Map;

public class ParkingLot {
    private final int totalSpots;
    private int occupiedSpots;
    private Map<Integer, Vehicle> spots;

    public ParkingLot(int totalSpots) {
        this.totalSpots = totalSpots;
        this.occupiedSpots = 0;
        this.spots = new HashMap<>();
    }

    public boolean parkVehicle(Vehicle vehicle) {
        if (occupiedSpots >= totalSpots) {
            return false; // Lot is full
        }
        for (Vehicle v : spots.values()) {
            if (v.getPlateNumber().equals(vehicle.getPlateNumber())) {
                return false; // Duplicate vehicle
            }
        }
        int spotId = findEmptySpot();
        spots.put(spotId, vehicle);
        vehicle.setEntryTime();
        occupiedSpots++;
        return true;
    }

    public Vehicle removeVehicle(String plateNumber) {
        for (Map.Entry<Integer, Vehicle> entry : spots.entrySet()) {
            if (entry.getValue().getPlateNumber().equals(plateNumber)) {
                Vehicle vehicle = entry.getValue();
                vehicle.setExitTime();
                spots.remove(entry.getKey());
                occupiedSpots--;
                return vehicle;
            }
        }
        return null; // Vehicle not found
    }

    public int checkAvailability() {
        return totalSpots - occupiedSpots;
    }

    public Map<Integer, Vehicle> getOccupiedList() {
        return new HashMap<>(spots);
    }

    private int findEmptySpot() {
        for (int i = 1; i <= totalSpots; i++) {
            if (!spots.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }
}