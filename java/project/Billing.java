import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Billing {
    private static final double BIKE_RATE = 1500.0; // $ per hour
    private static final double CAR_RATE = 2500.0;
    private static final double TRUCK_RATE = 3000.0;
    
    public double calculateFee(Vehicle vehicle) {
        long hours = Math.max(1, vehicle.getParkingDuration()); // Minimum 1 hour charge
        switch (vehicle.getVehicleType()) {
            case "bike":
                return hours * BIKE_RATE;
            case "car":
                return hours * CAR_RATE;
            case "truck":
                return hours * TRUCK_RATE;
            default:
                return 0.0;
        }
    }

    public void logBill(Vehicle vehicle, double fee) {
        try (FileWriter writer = new FileWriter("parking_log.txt", true)) {
            writer.write(String.format("Plate: %s, Type: %s, Duration: %d hours, Fee: MK%.2f, Time: %s%n",
                    vehicle.getPlateNumber(), vehicle.getVehicleType(), 
                    vehicle.getParkingDuration(), fee, new Date()));
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public String generateReceipt(Vehicle vehicle, double fee) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("\\documentclass{article}\n");
        receipt.append("\\usepackage{geometry}\n");
        receipt.append("\\geometry{a4paper, margin=1in}\n");
        receipt.append("\\begin{document}\n");
        receipt.append("\\begin{center}\n");
        receipt.append("\\textbf{Parking Receipt}\\\\\n");
        receipt.append("\\end{center}\n");
        receipt.append(String.format("Plate Number: %s\\\\\n", vehicle.getPlateNumber()));
        receipt.append(String.format("Vehicle Type: %s\\\\\n", vehicle.getVehicleType()));
        receipt.append(String.format("Entry Time: %s\\\\\n", new Date(vehicle.getEntryTime())));
        receipt.append(String.format("Exit Time: %s\\\\\n", new Date(vehicle.getExitTime())));
        receipt.append(String.format("Duration: %d hours\\\\\n", vehicle.getParkingDuration()));
        receipt.append(String.format("Total Fee: MK%.2f\\\\\n", fee));
        receipt.append("\\end{document}\n");
        return receipt.toString();
    }
}