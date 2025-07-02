import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;

public class MainApp {
    private ParkingLot parkingLot;
    private Billing billing;
    private JFrame frame;
    private JTextArea displayArea;
    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    public MainApp() {
        parkingLot = new ParkingLot(10); // 10 spots
        billing = new Billing();
        initializeGUI();
    }

    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Ignore and use default
        }
        frame = new JFrame("Smart Parking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        displayArea.setBackground(new Color(245, 245, 245));
        displayArea.setForeground(new Color(33, 37, 41));
        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Table for displaying occupied vehicles
        String[] columnNames = {"Spot", "Plate Number", "Type", "Entry Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        vehicleTable = new JTable(tableModel);
        vehicleTable.setFont(new Font("Arial", Font.PLAIN, 14));
        vehicleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        vehicleTable.setRowHeight(22);
        JScrollPane tableScroll = new JScrollPane(vehicleTable);
        tableScroll.setPreferredSize(new Dimension(650, 120));
        frame.add(tableScroll, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField plateField = new JTextField();
        plateField.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] vehicleTypes = {"bike", "car", "truck"};
        JComboBox<String> typeCombo = new JComboBox<>(vehicleTypes);
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        inputPanel.add(new JLabel("Plate Number:"));
        inputPanel.add(plateField);
        inputPanel.add(new JLabel("Vehicle Type:"));
        inputPanel.add(typeCombo);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton parkButton = new JButton("Park Vehicle");
        JButton exitButton = new JButton("Exit Vehicle");
        JButton checkButton = new JButton("Check Availability");
        for (JButton btn : new JButton[]{parkButton, exitButton, checkButton}) {
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setBackground(new Color(0, 123, 255));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }
        buttonPanel.add(parkButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(checkButton);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        parkButton.addActionListener(e -> {
            try {
                Vehicle vehicle = new Vehicle(plateField.getText(), (String)typeCombo.getSelectedItem());
                if (parkingLot.parkVehicle(vehicle)) {
                    displayArea.append("Parked: " + vehicle + "\n");
                    updateVehicleTable();
                } else {
                    displayArea.append("Failed to park: Lot full or duplicate plate\n");
                }
            } catch (IllegalArgumentException ex) {
                displayArea.append("Error: " + ex.getMessage() + "\n");
            }
        });

        exitButton.addActionListener(e -> {
            Vehicle vehicle = parkingLot.removeVehicle(plateField.getText());
            if (vehicle != null) {
                double fee = billing.calculateFee(vehicle);
                billing.logBill(vehicle, fee);
                displayArea.append("Vehicle exited: " + vehicle + "\n");
                displayArea.append("Fee: MK" + String.format("%.2f", fee) + "\n");
                saveReceipt(billing.generateReceipt(vehicle, fee));
                updateVehicleTable();
            } else {
                displayArea.append("Vehicle not found\n");
            }
        });

        checkButton.addActionListener(e -> {
            displayArea.append("Available spots: " + parkingLot.checkAvailability() + "\n");
            displayArea.append("Occupied vehicles:\n");
            for (Vehicle v : parkingLot.getOccupiedList().values()) {
                displayArea.append(v.toString() + "\n");
            }
            updateVehicleTable();
        });

        frame.setVisible(true);
    }

    private void updateVehicleTable() {
        tableModel.setRowCount(0);
        for (java.util.Map.Entry<Integer, Vehicle> entry : parkingLot.getOccupiedList().entrySet()) {
            Vehicle v = entry.getValue();
            tableModel.addRow(new Object[]{
                entry.getKey(),
                v.getPlateNumber(),
                v.getVehicleType(),
                new java.util.Date(v.getEntryTime())
            });
        }
    }

    private void saveReceipt(String latexContent) {
        try (FileWriter writer = new FileWriter("receipt.tex")) {
            writer.write(latexContent);
            displayArea.append("Receipt generated: receipt.tex\n");
        } catch (IOException e) {
            displayArea.append("Error generating receipt: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}