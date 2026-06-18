package Control;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import core.AirConditioner;
import core.Appliance;
import core.ApplianceCRUD;
import core.ElectricitySensor;
import core.Fan;
import core.LightDevice;
import core.LightSensor;
import core.Reading;
import core.Sensor;
import core.SensorCRUD;
import core.TemperatureSensor;
import io.FileManager;
import io.FileManager.DailyUsage;
import structure.*;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static Admin currentAdmin = null;
    private static Building building = null;
    private static SensorCRUD sensorCRUD = new SensorCRUD();
    private static ApplianceCRUD applianceCRUD = new ApplianceCRUD();
    private static ArrayList<Area> areaList = new ArrayList<>();

    // Default admin credentials
    private static final String DEFAULT_USERNAME = "Admin123";
    private static final String DEFAULT_PASSWORD = "1234";

    // ===== UTILITY METHODS =====

    static Area findAreaById(ArrayList<Area> areas, String areaId) {
        for (Area area : areas) {
            if (area.getRoomId().equals(areaId)) {
                return area;
            }
        }
        return null;
    }

    static Appliance createApplianceByType(String applianceType, String applianceId,
            String status, double energyThreshold,
            double energyConsumption) {
        if ("Light".equalsIgnoreCase(applianceType)) {
            return new LightDevice(applianceId, status, energyThreshold, energyConsumption);
        }
        if ("Air Conditioner".equalsIgnoreCase(applianceType) || "AC".equalsIgnoreCase(applianceType)) {
            return new AirConditioner(applianceId, status, energyThreshold, energyConsumption);
        }
        if ("Fan".equalsIgnoreCase(applianceType)) {
            return new Fan(applianceId, status, energyThreshold, energyConsumption);
        }
        return null;
    }

    // ===== ADMIN MENU METHODS =====

    private static void showMainMenu() {
        System.out.println("\n========================================");
        System.out.println("     GREEN CAMPUS MANAGEMENT SYSTEM     ");
        System.out.println("========================================");
        System.out.println("1. Login as Admin");
        System.out.println("2. Exit");
        System.out.println("========================================");
        System.out.print("Choose option: ");
    }

    private static void showAdminMenu() {
        System.out.println("\n========================================");
        System.out.println("        ADMIN CONTROL PANEL             ");
        System.out.println("========================================");
        System.out.println("1. Add/Remove Device");
        System.out.println("2. Add/Remove Sensor");
        System.out.println("3. Set Device Threshold");
        System.out.println("4. Modify Appliance (Turn ON/OFF)");
        System.out.println("5. View Building Status");
        System.out.println("6. Set Optimization Plan");
        System.out.println("7. Generate system_state.txt");
        System.out.println("8. Generate room_information.txt");
        System.out.println("9. Reload Data from Files");
        System.out.println("10. Logout");
        System.out.println("========================================");
        System.out.print("Choose option: ");
    }

    private static void showDeviceMenu() {
        System.out.println("\n--- Device Management ---");
        System.out.println("1. Add Device");
        System.out.println("2. Remove Device");
        System.out.println("3. List All Devices");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose option: ");
    }

    private static void showSensorMenu() {
        System.out.println("\n--- Sensor Management ---");
        System.out.println("1. Add Sensor");
        System.out.println("2. Remove Sensor");
        System.out.println("3. List All Sensors");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose option: ");
    }

    // ===== LOGIN =====

    private static boolean loginAdmin() {
        System.out.println("\n--- Admin Login ---");
        System.out.println("(Default credentials: Admin123 / 1234)");

        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty!");
                return false;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (password.isEmpty()) {
                System.out.println("Password cannot be empty!");
                return false;
            }

            // Check against default credentials
            if (username.equals(DEFAULT_USERNAME) && password.equals(DEFAULT_PASSWORD)) {
                currentAdmin = new Admin(username, password);
                System.out.println("Login successful! Welcome " + username + "!");

                // Load building data after successful login
                loadBuildingData();
                return true;
            } else {
                System.out.println("Invalid username or password!");
                System.out.println("Hint: Use username 'Admin123' and password '1234'");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== LOAD BUILDING DATA =====

    private static void loadBuildingData() {
        try {
            System.out.println("\nLoading building data from files...");

            // Reset existing data
            sensorCRUD = new SensorCRUD();
            applianceCRUD = new ApplianceCRUD();
            areaList = new ArrayList<>();
            building = new Building("Engineering Building", "123 University Ave");

            // Load sensors from file
            loadSensorsFromFile();

            // Load appliances from file
            loadAppliancesFromFile();

            // Load areas from file
            loadAreasFromFile();

            // Load sensor and appliance assignments from area_input.txt
            loadAreaAssignments();

            System.out.println("Building data loaded successfully!");
            System.out.println("Areas loaded: " + building.getAreas().size());
            System.out.println("Sensors loaded: " + sensorCRUD.getAllSensors().size());
            System.out.println("Appliances loaded: " + applianceCRUD.getAllAppliances().size());

            // Set the building for admin
            if (currentAdmin != null) {
                currentAdmin.setManagedBuilding(building);
            }

        } catch (IOException e) {
            System.err.println("Error loading building data: " + e.getMessage());
            System.err.println("Creating default building with sample data...");
            createDefaultBuilding();
        }
    }

    private static void loadSensorsFromFile() throws IOException {
        // Create sample sensors based on the existing sensor_input.txt content
        Sensor lSensor = new LightSensor("L101", 340.0, "2026-06-12T11:00:00");
        sensorCRUD.addSensor(lSensor);

        Sensor tSensor = new TemperatureSensor("T101", 23.0, "2026-06-12T11:05:00");
        sensorCRUD.addSensor(tSensor);

        Sensor eSensor = new ElectricitySensor("E101", 1.5, "2026-06-12T11:10:00");
        sensorCRUD.addSensor(eSensor);

        Sensor lSensor2 = new LightSensor("L102", 400.0, "2026-06-12T11:15:00");
        sensorCRUD.addSensor(lSensor2);

        Sensor lSensor3 = new LightSensor("L103", 350.0, "2026-06-12T11:15:00");
        sensorCRUD.addSensor(lSensor3);

        // Add test sensors
        Sensor lSensorTest = new LightSensor("LS01", 340.0, "2026-06-12T11:00:00");
        sensorCRUD.addSensor(lSensorTest);

        Sensor tSensorTest = new TemperatureSensor("TS01", 28.0, "2026-06-12T11:00:00");
        sensorCRUD.addSensor(tSensorTest);

        Sensor eSensorTest = new ElectricitySensor("ES01", 0.5, "2026-06-12T11:00:00");
        sensorCRUD.addSensor(eSensorTest);
    }

    private static void loadAppliancesFromFile() throws IOException {
        Appliance light = new LightDevice("LGT01", "OFF", 30, 13.0); // 13.0 > 30? No, it's below threshold
        applianceCRUD.register(light);

        Appliance ac = new AirConditioner("AC01", "OFF", 50, 44.3); // 44.3 < 50? No, it's below threshold
        applianceCRUD.register(ac);

        Appliance fan = new Fan("FAN01", "OFF", 20, 13.3); // 13.3 < 20? No, it's below threshold
        applianceCRUD.register(fan);

        Appliance fan2 = new Fan("FAN03", "ON", 20, 10.0); // 10.0 < 20? No, it's below threshold
        applianceCRUD.register(fan2);

        // Add some appliances that exceed threshold for optimization plan demo
        Appliance lightOver = new LightDevice("LGT02", "ON", 15, 18.5); // 18.5 > 15 - NEEDS OPTIMIZATION
        applianceCRUD.register(lightOver);

        Appliance acOver = new AirConditioner("AC02", "ON", 40, 52.0); // 52.0 > 40 - NEEDS OPTIMIZATION
        applianceCRUD.register(acOver);

        Appliance fanOver = new Fan("FAN04", "ON", 10, 15.0); // 15.0 > 10 - NEEDS OPTIMIZATION
        applianceCRUD.register(fanOver);
    }

    private static void loadAreasFromFile() throws IOException {
        // Create areas based on area_input.txt
        Area area1 = new Classroom("CR101", "Computer Science Lab");
        building.addArea(area1);
        areaList.add(area1);

        Area area2 = new LectureHall("LH201", "Main Lecture Hall");
        building.addArea(area2);
        areaList.add(area2);

        Area area3 = new Office("OF301", "Admin Office");
        building.addArea(area3);
        areaList.add(area3);

        Area area4 = new Corridor("CO401", "Block A Corridor");
        building.addArea(area4);
        areaList.add(area4);

        Area area5 = new Corridor("CO402", "Block B Corridor");
        building.addArea(area5);
        areaList.add(area5);

        // Add test area
        Area areaTest = new Classroom("AreaTest3", "test1234");
        building.addArea(areaTest);
        areaList.add(areaTest);
    }

    private static void loadAreaAssignments() throws IOException {
        // Assign sensors and appliances to areas based on area_input.txt
        Area cr101 = findAreaById(areaList, "CR101");
        Area lh201 = findAreaById(areaList, "LH201");
        Area of301 = findAreaById(areaList, "OF301");
        Area co402 = findAreaById(areaList, "CO402");
        Area areaTest = findAreaById(areaList, "AreaTest3");

        // Assign sensors to CR101
        if (cr101 != null) {
            Sensor l101 = sensorCRUD.getSensorBySerialNumber("L101");
            Sensor t101 = sensorCRUD.getSensorBySerialNumber("T101");
            Sensor e101 = sensorCRUD.getSensorBySerialNumber("E101");

            if (l101 != null)
                building.addSensorToArea(cr101, l101);
            if (t101 != null)
                building.addSensorToArea(cr101, t101);
            if (e101 != null)
                building.addSensorToArea(cr101, e101);

            // Assign appliances to CR101
            Appliance lgt01 = applianceCRUD.getAppliance("LGT01");
            Appliance ac01 = applianceCRUD.getAppliance("AC01");
            Appliance fan01 = applianceCRUD.getAppliance("FAN01");
            Appliance lgt02 = applianceCRUD.getAppliance("LGT02"); // Over threshold
            Appliance ac02 = applianceCRUD.getAppliance("AC02"); // Over threshold

            if (lgt01 != null)
                building.addApplianceToArea(cr101, lgt01);
            if (ac01 != null)
                building.addApplianceToArea(cr101, ac01);
            if (fan01 != null)
                building.addApplianceToArea(cr101, fan01);
            if (lgt02 != null)
                building.addApplianceToArea(cr101, lgt02);
            if (ac02 != null)
                building.addApplianceToArea(cr101, ac02);
        }

        // Assign sensors to LH201
        if (lh201 != null) {
            Sensor l102 = sensorCRUD.getSensorBySerialNumber("L102");
            if (l102 != null)
                building.addSensorToArea(lh201, l102);

            // Assign appliances to LH201
            Appliance lgt01 = applianceCRUD.getAppliance("LGT01");
            if (lgt01 != null)
                building.addApplianceToArea(lh201, lgt01);
        }

        // Assign sensors to OF301
        if (of301 != null) {
            Sensor t101 = sensorCRUD.getSensorBySerialNumber("T101");
            if (t101 != null)
                building.addSensorToArea(of301, t101);
        }

        // Assign sensors to CO402
        if (co402 != null) {
            Sensor l103 = sensorCRUD.getSensorBySerialNumber("L103");
            if (l103 != null)
                building.addSensorToArea(co402, l103);
        }

        // Assign sensors and appliances to AreaTest3
        if (areaTest != null) {
            Sensor ls01 = sensorCRUD.getSensorBySerialNumber("LS01");
            Sensor ts01 = sensorCRUD.getSensorBySerialNumber("TS01");
            Sensor es01 = sensorCRUD.getSensorBySerialNumber("ES01");

            if (ls01 != null)
                building.addSensorToArea(areaTest, ls01);
            if (ts01 != null)
                building.addSensorToArea(areaTest, ts01);
            if (es01 != null)
                building.addSensorToArea(areaTest, es01);

            Appliance fan03 = applianceCRUD.getAppliance("FAN03");
            Appliance fan04 = applianceCRUD.getAppliance("FAN04"); // Over threshold

            if (fan03 != null)
                building.addApplianceToArea(areaTest, fan03);
            if (fan04 != null)
                building.addApplianceToArea(areaTest, fan04);
        }
    }

    private static void createDefaultBuilding() {
        building = new Building("Engineering Building", "123 University Ave");

        // Create a default area
        Area defaultArea = new Classroom("CR101", "Computer Science Lab");
        building.addArea(defaultArea);
        areaList.add(defaultArea);

        // Create default sensors
        Sensor lSensor = new LightSensor("LS01", 340.0, LocalDateTime.now().toString());
        sensorCRUD.addSensor(lSensor);
        building.addSensorToArea(defaultArea, lSensor);

        Sensor tSensor = new TemperatureSensor("TS01", 28.0, LocalDateTime.now().toString());
        sensorCRUD.addSensor(tSensor);
        building.addSensorToArea(defaultArea, tSensor);

        // Create default appliances
        Appliance light = new LightDevice("LGT01", "OFF", 30, 10);
        applianceCRUD.register(light);
        building.addApplianceToArea(defaultArea, light);

        Appliance fan = new Fan("FAN01", "OFF", 20, 10);
        applianceCRUD.register(fan);
        building.addApplianceToArea(defaultArea, fan);

        if (currentAdmin != null) {
            currentAdmin.setManagedBuilding(building);
        }
        System.out.println("Default building created with one area.");
    }

    // ===== DEVICE MANAGEMENT =====

    private static void manageDevices() {
        while (true) {
            showDeviceMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        addDevice();
                        break;
                    case 2:
                        removeDevice();
                        break;
                    case 3:
                        listDevices();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private static void addDevice() {
        try {
            System.out.print("Enter area ID to add device to: ");
            String areaId = scanner.nextLine().trim();
            Area target = findAreaById(areaList, areaId);

            if (target == null) {
                System.out.println("Area not found!");
                return;
            }

            System.out.print("Enter device type (Light/Air Conditioner/Fan): ");
            String deviceType = scanner.nextLine().trim();

            System.out.print("Enter device ID: ");
            String deviceId = scanner.nextLine().trim();

            if (applianceCRUD.getAppliance(deviceId) != null) {
                System.out.println("Device ID already exists!");
                return;
            }

            System.out.print("Enter initial status (ON/OFF): ");
            String status = scanner.nextLine().trim().toUpperCase();

            if (!status.equals("ON") && !status.equals("OFF")) {
                System.out.println("Invalid status! Use ON or OFF.");
                return;
            }

            System.out.print("Enter energy threshold (kWh): ");
            double threshold = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter initial energy consumption (kWh): ");
            double consumption = Double.parseDouble(scanner.nextLine().trim());

            Appliance appliance = createApplianceByType(deviceType, deviceId, status, threshold, consumption);

            if (appliance != null) {
                applianceCRUD.register(appliance);
                building.addApplianceToArea(target, appliance);
                FileManager.writeRoomInformation(building, "Added device: " + deviceId);
                System.out.println("Device added successfully!");
            } else {
                System.out.println("Unsupported device type!");
            }

        } catch (IOException e) {
            System.err.println("Error adding device: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
    }

    private static void removeDevice() {
        try {
            System.out.print("Enter area ID to remove device from: ");
            String areaId = scanner.nextLine().trim();
            Area target = findAreaById(areaList, areaId);

            if (target == null) {
                System.out.println("Area not found!");
                return;
            }

            System.out.print("Enter device ID to remove: ");
            String deviceId = scanner.nextLine().trim();

            Appliance appliance = applianceCRUD.getAppliance(deviceId);

            if (appliance == null) {
                System.out.println("Device not found!");
                return;
            }

            if (applianceCRUD.remove(deviceId)) {
                building.removeApplianceFromArea(target, appliance);
                FileManager.writeRoomInformation(building, "Removed device: " + deviceId);
                System.out.println("Device removed successfully!");
            } else {
                System.out.println("Failed to remove device!");
            }

        } catch (IOException e) {
            System.err.println("Error removing device: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void listDevices() {
        System.out.println("\n--- All Devices ---");
        Vector<Appliance> appliances = applianceCRUD.getAllAppliances();
        if (appliances.isEmpty()) {
            System.out.println("No devices registered.");
        } else {
            for (Appliance app : appliances) {
                System.out.printf("ID: %-8s | Type: %-16s | Status: %-4s | Consumed: %.2f kWh | Threshold: %.2f kWh%n",
                        app.getApplianceID(), app.getApplianceType(),
                        app.getStatus(), app.getEnergyConsumption(), app.getEnergyThreshold());
            }
        }
    }

    // ===== SENSOR MANAGEMENT =====

    private static void manageSensors() {
        while (true) {
            showSensorMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        addSensor();
                        break;
                    case 2:
                        removeSensor();
                        break;
                    case 3:
                        listSensors();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private static void addSensor() {
        try {
            System.out.print("Enter area ID to add sensor to: ");
            String areaId = scanner.nextLine().trim();
            Area target = findAreaById(areaList, areaId);

            if (target == null) {
                System.out.println("Area not found!");
                return;
            }

            System.out.print("Enter sensor type (Light/Temperature/Electricity): ");
            String sensorType = scanner.nextLine().trim();

            System.out.print("Enter sensor ID: ");
            String sensorId = scanner.nextLine().trim();

            if (sensorCRUD.getSensorBySerialNumber(sensorId) != null) {
                System.out.println("Sensor ID already exists!");
                return;
            }

            System.out.print("Enter initial reading value: ");
            double value = Double.parseDouble(scanner.nextLine().trim());

            String timestamp = LocalDateTime.now().toString();

            Sensor sensor = null;
            switch (sensorType.toLowerCase()) {
                case "light":
                    sensor = new LightSensor(sensorId, value, timestamp);
                    break;
                case "temperature":
                    sensor = new TemperatureSensor(sensorId, value, timestamp);
                    break;
                case "electricity":
                    sensor = new ElectricitySensor(sensorId, value, timestamp);
                    break;
                default:
                    System.out.println("Unsupported sensor type!");
                    return;
            }

            sensorCRUD.addSensor(sensor);
            building.addSensorToArea(target, sensor);
            FileManager.writeRoomInformation(building, "Added sensor: " + sensorId);
            System.out.println("Sensor added successfully!");

        } catch (IOException e) {
            System.err.println("Error adding sensor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
    }

    private static void removeSensor() {
        try {
            System.out.print("Enter area ID to remove sensor from: ");
            String areaId = scanner.nextLine().trim();
            Area target = findAreaById(areaList, areaId);

            if (target == null) {
                System.out.println("Area not found!");
                return;
            }

            System.out.print("Enter sensor ID to remove: ");
            String sensorId = scanner.nextLine().trim();

            Sensor sensor = sensorCRUD.getSensorBySerialNumber(sensorId);

            if (sensor == null) {
                System.out.println("Sensor not found!");
                return;
            }

            sensorCRUD.removeSensor(sensor);
            building.removeSensorFromArea(target, sensor);
            FileManager.writeRoomInformation(building, "Removed sensor: " + sensorId);
            System.out.println("Sensor removed successfully!");

        } catch (IOException e) {
            System.err.println("Error removing sensor: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void listSensors() {
        System.out.println("\n--- All Sensors ---");
        Vector<Sensor> sensors = sensorCRUD.getAllSensors();
        if (sensors.isEmpty()) {
            System.out.println("No sensors registered.");
        } else {
            for (Sensor s : sensors) {
                Reading latest = s.getLatestReading();
                String valStr = (latest != null) ? latest.getValue() + " " + s.getUnit() : "No readings";
                System.out.printf("ID: %-8s | Type: %-14s | Latest: %-12s%n",
                        s.getSensorID(), s.getSensorType(), valStr);
            }
        }
    }

    // ===== THRESHOLD MANAGEMENT =====

    private static void setDeviceThreshold() {
        try {
            System.out.print("Enter device ID: ");
            String deviceId = scanner.nextLine().trim();

            Appliance appliance = applianceCRUD.getAppliance(deviceId);

            if (appliance == null) {
                System.out.println("Device not found!");
                return;
            }

            System.out.println("Current threshold: " + appliance.getEnergyThreshold() + " kWh");
            System.out.println("Current consumption: " + appliance.getEnergyConsumption() + " kWh");

            System.out.print("Enter new energy threshold (kWh): ");
            double threshold = Double.parseDouble(scanner.nextLine().trim());

            if (threshold < 0) {
                System.out.println("Threshold cannot be negative!");
                return;
            }

            appliance.setEnergyThreshold(threshold);
            System.out.println("Threshold updated successfully!");
            System.out.println("Current threshold for " + deviceId + ": " + threshold + " kWh");

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
        }
    }

    // ===== APPLIANCE CONTROL =====

    private static void modifyAppliance() {
        try {
            System.out.print("Enter device ID: ");
            String deviceId = scanner.nextLine().trim();

            Appliance appliance = applianceCRUD.getAppliance(deviceId);

            if (appliance == null) {
                System.out.println("Device not found!");
                return;
            }

            System.out.println("Current status: " + appliance.getStatus());
            System.out.print("Enter new status (ON/OFF): ");
            String newStatus = scanner.nextLine().trim().toUpperCase();

            if (!newStatus.equals("ON") && !newStatus.equals("OFF")) {
                System.out.println("Invalid status! Use ON or OFF.");
                return;
            }

            if (newStatus.equals("ON")) {
                appliance.turnOn(currentAdmin);
            } else {
                appliance.turnOff(currentAdmin);
            }

            FileManager.writeRoomInformation(building, "Modified appliance: " + deviceId + " -> " + newStatus);
            System.out.println("Device status updated to: " + newStatus);

        } catch (IOException e) {
            System.err.println("Error modifying appliance: " + e.getMessage());
        }
    }

    // ===== VIEW BUILDING STATUS =====

    private static void viewBuildingStatus() {
        if (building == null) {
            System.out.println("No building loaded!");
            return;
        }

        System.out.println("\n" + building);
        building.listAllAreas();

        // Show detailed information for each area
        for (Area area : building.getAreas()) {
            System.out.println(area.toDetailedString());
        }
    }

    // ===== OPTIMIZATION PLAN =====

    private static void setOptimizationPlan() {
        System.out.println("\n========================================");
        System.out.println("        OPTIMIZATION PLAN               ");
        System.out.println("========================================");

        if (building == null) {
            System.out.println("No building loaded!");
            return;
        }

        System.out.println("Building: " + building.getName());
        System.out.println("Address : " + building.getAddress());
        System.out.println("========================================");

        boolean hasOptimizationNeeded = false;
        int totalOptimized = 0;
        double totalPotentialSavings = 0.0;

        for (Area area : building.getAreas()) {
            Vector<Appliance> appliances = area.getAppliances();
            boolean areaHasIssues = false;

            for (Appliance app : appliances) {
                double consumption = app.getEnergyConsumption();
                double threshold = app.getEnergyThreshold();

                // Check if appliance needs optimization
                if (consumption > threshold) {
                    if (!areaHasIssues) {
                        System.out.println("\n📍 Area: " + area.getName() + " (" + area.getAreaId() + ")");
                        System.out.println("   Type: " + area.getAreaType());
                        System.out.println("   ----------------------------------------");
                        areaHasIssues = true;
                        hasOptimizationNeeded = true;
                    }

                    double excess = consumption - threshold;
                    double savingsPercentage = (excess / consumption) * 100;
                    totalPotentialSavings += excess;
                    totalOptimized++;

                    System.out.println("   ⚠️  Appliance: " + app.getApplianceID());
                    System.out.println("      Type: " + app.getApplianceType());
                    System.out.println("      Status: " + app.getStatus());
                    System.out.println("      Current Consumption: " + String.format("%.2f", consumption) + " kWh");
                    System.out.println("      Threshold: " + String.format("%.2f", threshold) + " kWh");
                    System.out.println("      Excess: " + String.format("%.2f", excess) + " kWh");
                    System.out.println("      Potential Savings: " + String.format("%.1f", savingsPercentage) + "%");

                    // Generate specific optimization recommendation based on appliance type and
                    // area
                    System.out.print("      ✅ Recommendation: ");
                    String recommendation = generateRecommendation(app, area);
                    System.out.println(recommendation);
                    System.out.println("   ----------------------------------------");
                }
            }
        }

        // Summary section
        System.out.println("\n========================================");
        System.out.println("        OPTIMIZATION SUMMARY            ");
        System.out.println("========================================");

        if (!hasOptimizationNeeded) {
            System.out.println("✅ All appliances are operating within their energy thresholds!");
            System.out.println("   No optimization needed at this time.");
        } else {
            System.out.println("⚠️  Appliances needing optimization: " + totalOptimized);
            System.out.println(
                    "   Total potential energy savings: " + String.format("%.2f", totalPotentialSavings) + " kWh");
            System.out.println("   Estimated cost savings: RM " + String.format("%.2f", totalPotentialSavings * 0.50));
            System.out.println("\n   Recommendations:");
            System.out.println("   1. Review and adjust energy thresholds for appliances");
            System.out.println("   2. Implement scheduled maintenance for high-consumption devices");
            System.out.println("   3. Consider upgrading to energy-efficient appliances");
            System.out.println("   4. Monitor appliance usage patterns and adjust accordingly");
            System.out.println("   5. Implement automated scheduling for appliance usage");
        }
        System.out.println("========================================");
    }

    /**
     * Generate specific recommendation based on appliance type and area
     * 
     * @param app  The appliance that needs optimization
     * @param area The area where the appliance is located
     * @return A specific recommendation string
     */
    private static String generateRecommendation(Appliance app, Area area) {
        String areaId = area.getAreaId();
        String applianceId = app.getApplianceID();
        double consumption = app.getEnergyConsumption();
        double threshold = app.getEnergyThreshold();
        double excess = consumption - threshold;

        if (app instanceof LightDevice) {
            if (app.getStatus().equals("ON")) {
                if (excess > 5) {
                    return "Turn off unnecessary lights in area " + areaId + " (Appliance: " + applianceId +
                            "). Consider installing motion sensors or dimmer switches. Current consumption exceeds threshold by "
                            +
                            String.format("%.2f", excess) + " kWh.";
                } else {
                    return "Consider dimming lights or using natural light in area " + areaId +
                            " (Appliance: " + applianceId + "). Switch to LED bulbs for better energy efficiency.";
                }
            } else {
                return "Check for faulty light fixtures in area " + areaId + " (Appliance: " + applianceId +
                        "). Even when OFF, it shows high consumption. Consider replacing with energy-efficient LED lights.";
            }
        } else if (app instanceof AirConditioner) {
            if (app.getStatus().equals("ON")) {
                if (excess > 10) {
                    return "Increase temperature setting by 3-5°C in area " + areaId + " (Appliance: " + applianceId +
                            "). Use fan mode when possible. Clean or replace air filters to improve efficiency. Excess consumption: "
                            +
                            String.format("%.2f", excess) + " kWh.";
                } else {
                    return "Increase temperature setting by 2-3°C or use fan mode in area " + areaId +
                            " (Appliance: " + applianceId + "). Ensure windows and doors are properly sealed.";
                }
            } else {
                return "Check for air leaks or service the AC unit in area " + areaId + " (Appliance: " + applianceId +
                        "). Even when OFF, it shows high consumption. Consider scheduling maintenance or replacing old units.";
            }
        } else if (app instanceof Fan) {
            if (app.getStatus().equals("ON")) {
                if (excess > 3) {
                    return "Reduce fan speed or use timer to turn off automatically in area " + areaId +
                            " (Appliance: " + applianceId
                            + "). Consider replacing with energy-efficient BLDC fans. Excess: " +
                            String.format("%.2f", excess) + " kWh.";
                } else {
                    return "Reduce fan speed or use timer mode in area " + areaId + " (Appliance: " + applianceId +
                            "). Turn off fans when area is not in use.";
                }
            } else {
                return "Check for faulty fan motor in area " + areaId + " (Appliance: " + applianceId +
                        "). Even when OFF, it shows high consumption. Consider replacing with energy-efficient model.";
            }
        } else {
            // Generic appliance recommendation
            if (app.getStatus().equals("ON")) {
                return "Review usage pattern of " + app.getApplianceType() + " (" + applianceId + ") in area " + areaId
                        +
                        ". Consider scheduling usage during off-peak hours or replacing with energy-efficient model. Excess consumption: "
                        +
                        String.format("%.2f", excess) + " kWh.";
            } else {
                return "Check " + app.getApplianceType() + " (" + applianceId + ") in area " + areaId +
                        " for faults. Even when OFF, it shows high consumption. Consider maintenance or replacement.";
            }
        }
    }

    // ===== GENERATE REPORTS =====

    private static void generateSystemState() {
        try {
            // Aggregate daily electricity consumption
            Vector<DailyUsage> dailyList = new Vector<>();
            for (Appliance app : applianceCRUD.getAllAppliances()) {
                for (Reading reading : app.getEnergyUsageHistory()) {
                    String timestamp = reading.getTimestamp();
                    String date = (timestamp != null && timestamp.length() >= 10)
                            ? timestamp.substring(0, 10)
                            : "Unknown Date";
                    boolean found = false;
                    for (int i = 0; i < dailyList.size(); i++) {
                        DailyUsage du = dailyList.elementAt(i);
                        if (du.date.equals(date)) {
                            du.consumption += reading.getValue();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        dailyList.addElement(new DailyUsage(date, reading.getValue()));
                    }
                }
            }

            FileManager.writeSystemState(sensorCRUD.getAllSensors(),
                    applianceCRUD.getAllAppliances(),
                    dailyList);
            System.out.println("system_state.txt generated successfully!");

        } catch (IOException e) {
            System.err.println("Error generating system state: " + e.getMessage());
        }
    }

    private static void generateRoomInformation() {
        try {
            if (building == null) {
                System.out.println("No building loaded!");
                return;
            }
            FileManager.writeRoomInformation(building, "Manual generation by admin");
            System.out.println("room_information.txt generated successfully!");
        } catch (IOException e) {
            System.err.println("Error generating room information: " + e.getMessage());
        }
    }

    // ===== RELOAD DATA =====

    private static void reloadDataFromFiles() {
        System.out.println("\n--- Reloading Data ---");
        loadBuildingData();
    }

    // ===== MAIN =====

    public static void main(String[] args) {
        System.out.println("Welcome to Green Campus Management System!");
        System.out.println("Default admin credentials: Admin123 / 1234");

        // Create building first
        building = new Building("Engineering Building", "123 University Ave");

        while (true) {
            if (currentAdmin == null) {
                showMainMenu();
                try {
                    int choice = Integer.parseInt(scanner.nextLine().trim());

                    switch (choice) {
                        case 1:
                            if (loginAdmin()) {
                                System.out.println("\n--- Building Status After Login ---");
                                viewBuildingStatus();
                            }
                            break;
                        case 2:
                            System.out.println("Exiting system. Goodbye!");
                            scanner.close();
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Invalid option!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number!");
                }
            } else {
                showAdminMenu();
                try {
                    int choice = Integer.parseInt(scanner.nextLine().trim());

                    switch (choice) {
                        case 1:
                            manageDevices();
                            break;
                        case 2:
                            manageSensors();
                            break;
                        case 3:
                            setDeviceThreshold();
                            break;
                        case 4:
                            modifyAppliance();
                            break;
                        case 5:
                            viewBuildingStatus();
                            break;
                        case 6:
                            setOptimizationPlan();
                            break;
                        case 7:
                            generateSystemState();
                            break;
                        case 8:
                            generateRoomInformation();
                            break;
                        case 9:
                            reloadDataFromFiles();
                            break;
                        case 10:
                            currentAdmin = null;
                            System.out.println("Logged out successfully!");
                            break;
                        default:
                            System.out.println("Invalid option!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number!");
                }
            }
        }
    }
}