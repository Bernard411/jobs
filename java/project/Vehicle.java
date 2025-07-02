public class Vehicle {
    private String plateNumber;
    private String vehicleType; // bike, car, truck
    private long entryTime;
    private long exitTime;
    private static final String PLATE_PATTERN = "[A-Z0-9]{1,10}"; // Simple plate number validation

    public Vehicle(String plateNumber, String vehicleType) {
        if (!plateNumber.matches(PLATE_PATTERN)) {
            throw new IllegalArgumentException("Invalid plate number format");
        }
        if (!vehicleType.equals("bike") && !vehicleType.equals("car") && !vehicleType.equals("truck")) {
            throw new IllegalArgumentException("Invalid vehicle type");
        }
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
    }

    public void setEntryTime() {
        this.entryTime = System.currentTimeMillis();
    }

    public void setExitTime() {
        this.exitTime = System.currentTimeMillis();
    }

    public long getParkingDuration() {
        if (exitTime == 0) {
            return (System.currentTimeMillis() - entryTime) / (1000 * 60 * 60); // hours
        }
        return (exitTime - entryTime) / (1000 * 60 * 60); // hours
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public long getEntryTime() {
        return this.entryTime;
    }

    public long getExitTime() {
        return this.exitTime;
    }

    @Override
    public String toString() {
        return "Vehicle [Plate: " + plateNumber + ", Type: " + vehicleType + 
               ", Entry: " + new java.util.Date(entryTime) + 
               (exitTime > 0 ? ", Exit: " + new java.util.Date(exitTime) : "") + "]";
    }
}