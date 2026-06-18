package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Vector;

import core.Appliance;
import core.Reading;
import core.Sensor;
import structure.Area;
import structure.Building;

public class FileManager {

    // File paths
    private static final String SENSOR_INPUT = "sensor_input.txt";
    private static final String APPLIANCE_INPUT = "appliance_input.txt";
    private static final String AREA_INPUT = "area_input.txt";
    private static final String SYSTEM_STATE = "system_state.txt";
    private static final String ROOM_INFO = "room_information.txt";
    private static final String ADMIN_FILE = "admin_accounts.txt";

    /**
     * Read sensor data from file
     */
    public static void readSensorData() throws IOException {
        File file = new File(SENSOR_INPUT);
        if (!file.exists()) {
            throw new IOException("Sensor input file not found: " + SENSOR_INPUT);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#"))
                    continue;
                // Process line - this would be handled by the caller
            }
        }
    }

    /**
     * Read appliance data from file
     */
    public static void readApplianceData() throws IOException {
        File file = new File(APPLIANCE_INPUT);
        if (!file.exists()) {
            throw new IOException("Appliance input file not found: " + APPLIANCE_INPUT);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#"))
                    continue;
                // Process line - this would be handled by the caller
            }
        }
    }

    /**
     * Read area data from file
     */
    public static void readAreaData() throws IOException {
        File file = new File(AREA_INPUT);
        if (!file.exists()) {
            throw new IOException("Area input file not found: " + AREA_INPUT);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#"))
                    continue;
                // Process line - this would be handled by the caller
            }
        }
    }

    /**
     * Write room information to file
     */
    public static void writeRoomInformation(Building building, String triggerEvent) throws IOException {
        File roomInfoFile = new File(ROOM_INFO);
        try (PrintWriter writer = new PrintWriter(new FileWriter(roomInfoFile))) {
            writer.println("==========================================");
            writer.println("       ROOM INFORMATION REPORT            ");
            writer.println("==========================================");
            writer.println("Generated : " + LocalDateTime.now());
            writer.println("Trigger   : " + triggerEvent);
            writer.println();
            writer.println("Building  : " + building.getName());
            writer.println("Address   : " + building.getAddress());
            writer.println("Areas     : " + building.getAreas().size());
            writer.println("------------------------------------------");

            for (Area area : building.getAreas()) {
                writer.println();
                writer.println(area.toDetailedString());
                writer.println("  ........................................");
            }
            writer.println("==========================================");
        }
    }

    /**
     * Write system state to file
     */
    public static void writeSystemState(Vector<Sensor> sensors, Vector<Appliance> appliances,
            Vector<DailyUsage> dailyList) throws IOException {
        File outputFile = new File(SYSTEM_STATE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("==========================================");
            writer.println("         GREEN CAMPUS SYSTEM STATE        ");
            writer.println("==========================================");
            writer.println("Report Date: " + LocalDateTime.now());
            writer.println();

            // Sensors summary
            writer.println("--- SENSORS STATE ---");
            if (sensors.isEmpty()) {
                writer.println("No sensors registered.");
            } else {
                for (Sensor s : sensors) {
                    Reading latest = s.getLatestReading();
                    String valStr = (latest != null) ? latest.getValue() + " " + s.getUnit() : "No readings";
                    String timeStr = (latest != null) ? latest.getTimestamp() : "N/A";
                    writer.printf("Sensor ID: %-8s | Type: %-12s | Latest: %-12s | Time: %s%n",
                            s.getSensorID(), s.getSensorType(), valStr, timeStr);
                }
            }
            writer.println();

            // Appliances summary
            writer.println("--- APPLIANCES STATE ---");
            if (appliances.isEmpty()) {
                writer.println("No appliances registered.");
            } else {
                for (Appliance app : appliances) {
                    writer.printf("Appliance ID: %-8s | Type: %-16s | Status: %-4s | Total Consumed: %.2f kWh%n",
                            app.getApplianceID(), app.getApplianceType(), app.getStatus(),
                            app.getEnergyConsumption());
                }
            }
            writer.println();

            // Daily log
            writer.println("--- DAILY ELECTRICITY CONSUMPTION LOG ---");
            if (dailyList.isEmpty()) {
                writer.println("No electricity consumption records found.");
            } else {
                for (DailyUsage du : dailyList) {
                    writer.printf("Date: %s | Total Consumption: %.2f kWh%n", du.date, du.consumption);
                }
            }
            writer.println("==========================================");
        }
    }

    /**
     * Save admin account to file
     */
    public static void saveAdminAccount(String username, String password) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ADMIN_FILE, true))) {
            writer.println(username + "," + password);
        }
    }

    /**
     * Check if admin account exists
     */
    public static boolean adminExists(String username) throws IOException {
        File file = new File(ADMIN_FILE);
        if (!file.exists())
            return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validate admin login
     */
    public static boolean validateAdminLogin(String username, String password) throws IOException {
        File file = new File(ADMIN_FILE);
        if (!file.exists())
            return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Inner class for daily usage
    public static class DailyUsage {
        public String date;
        public double consumption;

        public DailyUsage(String date, double consumption) {
            this.date = date;
            this.consumption = consumption;
        }
    }
}