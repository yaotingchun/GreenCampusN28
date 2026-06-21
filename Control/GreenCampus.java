package control;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;
import structure.*;

public class GreenCampus {

    // -----------------------------------------------------------------------
    // Helper: write a snapshot of all areas to room_information.txt.
    // Call this after every addSensor / removeSensor /
    // addAppliance / removeAppliance operation.
    // -----------------------------------------------------------------------
    public static void writeRoomInformation(Building building, String triggerEvent) {
        File roomInfoFile = new File("room_information.txt");
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
            System.out.println("room_information.txt updated. [" + triggerEvent + "]");
        } catch (IOException e) {
            System.err.println("Error writing room_information.txt: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Utility: find an Area by roomId within a list
    // -----------------------------------------------------------------------
    static Area findAreaById(ArrayList<Area> areas, String areaId) {
        for (Area area : areas) {
            if (area.getRoomId().equals(areaId)) {
                return area;
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Helper: create an appliance instance from a file record.
    // -----------------------------------------------------------------------
    static Appliance createApplianceByType(String applianceType, String applianceId, String status, double energyThreshold, double energyConsumption) {
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

    // -----------------------------------------------------------------------
    // MAIN
    // -----------------------------------------------------------------------
    public static void main(String[] args) {
        SensorCRUD sensorCRUD = new SensorCRUD();
        ApplianceCRUD applianceCRUD = new ApplianceCRUD();

        // ===================================================================
        // 1. Read sensor readings from sensor_input.txt
        // ===================================================================
        File sensorFile = new File("sensor_input.txt");
        if (sensorFile.exists()) {
            System.out.println("Reading sensor data from " + sensorFile.getName() + "...");
            try (BufferedReader reader = new BufferedReader(new FileReader(sensorFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#"))
                        continue;
                    String[] parts = line.split(",");
                    if (parts.length < 4)
                        continue;
                    String type = parts[0].trim();
                    String id = parts[1].trim();
                    double value = Double.parseDouble(parts[2].trim());
                    String timestamp = parts[3].trim();

                    Sensor existing = sensorCRUD.getSensorBySerialNumber(id);
                    if (existing != null) {
                        existing.addReading(value, timestamp);
                    } else {
                        Sensor newSensor = null;
                        if ("Light".equalsIgnoreCase(type)) {
                            newSensor = new LightSensor(id, value, timestamp);
                        } else if ("Temperature".equalsIgnoreCase(type)) {
                            newSensor = new TemperatureSensor(id, value, timestamp);
                        } else if ("Electricity".equalsIgnoreCase(type)) {
                            newSensor = new ElectricitySensor(id, value, timestamp);
                        }
                        if (newSensor != null) {
                            sensorCRUD.addSensor(newSensor);
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error parsing sensor input: " + e.getMessage());
            }
        } else {
            System.out.println("No input file sensor_input.txt found.");
        }

        // ===================================================================
        // 2. Read appliance definitions and usage logs from appliance_input.txt
        // ===================================================================
        File applianceFile = new File("appliance_input.txt");
        if (applianceFile.exists()) {
            System.out.println("Reading appliance data from " + applianceFile.getName() + "...");
            try (BufferedReader reader = new BufferedReader(new FileReader(applianceFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#"))
                        continue;
                    String[] parts = line.split(",");
                    if (parts.length < 2) {
                        continue;
                    }

                    String recordType = parts[0].trim();
                    if ("APPLIANCE".equalsIgnoreCase(recordType) && parts.length >= 4) {
                        String applianceType = parts[1].trim();
                        String applianceId = parts[2].trim();
                        String status = parts[3].trim();
                        double energyThreshold = Double.parseDouble((parts.length >= 5) ? parts[4].trim() : "0.0");
                        double energyConsumption = Double.parseDouble((parts.length >= 6) ? parts[5].trim() : "0.0");
                        if (applianceCRUD.getAppliance(applianceId) == null) {
                            Appliance appliance = createApplianceByType(applianceType, applianceId, status, energyThreshold, energyConsumption);
                            if (appliance != null) {
                                applianceCRUD.register(appliance);
                                System.out.println("  Loaded appliance: " + appliance);
                            } else {
                                System.out.println("  [WARN] Unsupported appliance type " + applianceType + ".");
                            }
                        }
                    } else if ("USAGE".equalsIgnoreCase(recordType) && parts.length >= 4) {
                        String applianceId = parts[1].trim();
                        double energyUsed = Double.parseDouble(parts[2].trim());
                        String timestamp = parts[3].trim();
                        Appliance appliance = applianceCRUD.getAppliance(applianceId);
                        if (appliance != null) {
                            appliance.addEnergyUsage(energyUsed, timestamp);
                        } else {
                            System.out.println("  [WARN] Appliance " + applianceId + " not found for usage record.");
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error parsing appliance input: " + e.getMessage());
            }
        } else {
            System.out.println("No appliance_input.txt found.");
        }

        Appliance fan2 = applianceCRUD.getAppliance("FAN03");

        // ===================================================================
        // 3. Build the building from area_input.txt
        // Each SENSOR / APPLIANCE line calls the wrapper so
        // room_information.txt is written on every add/remove.
        // ===================================================================
        Building building = new Building("Engineering Building", "123 University Ave");
        ArrayList<Area> areaList = new ArrayList<>();

        Area areaTest = new Classroom("AreaTest3", "test1234");
        Sensor lSensor = new LightSensor("LS01", 340.0, "2026-06-12T11:00:00");

        building.addSensorToArea(areaTest, lSensor);
        sensorCRUD.addSensor(lSensor);
        Sensor tSensor = new TemperatureSensor("TS01", 28.0, "2026-06-12T11:00:00");
        building.addSensorToArea(areaTest, tSensor);
        Sensor eSensor = new ElectricitySensor("ES01", 0.5, "2026-06-12T11:00:00");
        building.addSensorToArea(areaTest, eSensor);

        building.addArea(areaTest);
        areaList.add(areaTest);
        if (fan2 != null) {
            building.addApplianceToArea(areaTest, fan2);
        }

        File areaFile = new File("area_input.txt");
        if (areaFile.exists()) {
            System.out.println("Reading area data from " + areaFile.getName() + "...");
            try (BufferedReader reader = new BufferedReader(new FileReader(areaFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#"))
                        continue;
                    String[] parts = line.split(",");

                    if (parts[0].trim().equalsIgnoreCase("AREA") && parts.length >= 4) {
                        String areaId = parts[1].trim();
                        String areaName = parts[2].trim();
                        String areaType = parts[3].trim();
                        Area area;
                        switch (areaType) {
                            case "LectureHall":
                                area = new LectureHall(areaId, areaName);
                                break;
                            case "Office":
                                area = new Office(areaId, areaName);
                                break;
                            case "Corridor":
                                area = new Corridor(areaId, areaName);
                                break;
                            default:
                                area = new Classroom(areaId, areaName);
                                break;
                        }
                        building.addArea(area);
                        areaList.add(area);
                        System.out.println("  Loaded area: " + area);

                    } else if (parts[0].trim().equalsIgnoreCase("SENSOR") && parts.length >= 3) {
                        String areaId = parts[1].trim();
                        String sensorId = parts[2].trim();
                        Area target = findAreaById(areaList, areaId);
                        Sensor sensor = sensorCRUD.getSensorBySerialNumber(sensorId);
                        if (target != null && sensor != null) {
                            building.addSensorToArea(target, sensor);
                        } else {
                            System.out.println("  [WARN] Sensor " + sensorId + " or area " + areaId + " not found.");
                        }

                    } else if (parts[0].trim().equalsIgnoreCase("APPLIANCE") && parts.length >= 3) {
                        String areaId = parts[1].trim();
                        String applianceId = parts[2].trim();
                        Area target = findAreaById(areaList, areaId);
                        Appliance appliance = applianceCRUD.getAppliance(applianceId);
                        if (target != null && appliance != null) {
                            building.addApplianceToArea(target, appliance);
                        } else {
                            System.out.println(
                                    "  [WARN] Appliance " + applianceId + " or area " + areaId + " not found.");
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error parsing area input: " + e.getMessage());
            }
        } else {
            // Fallback: single default area, assign all sensors & appliances
            System.out.println("No area_input.txt found. Using default area.");
            Area defaultArea = new Classroom("CR101", "Computer Science Classroom");
            building.addArea(defaultArea);
            areaList.add(defaultArea);

            for (Sensor s : sensorCRUD.getAllSensors()) {
                building.addSensorToArea(defaultArea, s);
            }
            for (Appliance appliance : applianceCRUD.getAllAppliances()) {
                building.addApplianceToArea(defaultArea, appliance);
            }
        }

        System.out.println("\n" + building);
        building.listAllAreas();

        // ===================================================================
        // 4. Aggregate daily electricity consumption (bubble sort, no Map)
        // ===================================================================
        class DailyUsage {
            String date;
            double consumption;

            DailyUsage(String date, double consumption) {
                this.date = date;
                this.consumption = consumption;
            }
        }
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
        for (int i = 0; i < dailyList.size() - 1; i++) {
            for (int j = 0; j < dailyList.size() - i - 1; j++) {
                if (dailyList.elementAt(j).date.compareTo(dailyList.elementAt(j + 1).date) > 0) {
                    DailyUsage temp = dailyList.elementAt(j);
                    dailyList.setElementAt(dailyList.elementAt(j + 1), j);
                    dailyList.setElementAt(temp, j + 1);
                }
            }
        }

        // ===================================================================
        // 5. Write system_state.txt
        // ===================================================================
        File outputFile = new File("system_state.txt");
        System.out.println("\nSaving current state to " + outputFile.getName() + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("==========================================");
            writer.println("         GREEN CAMPUS SYSTEM STATE        ");
            writer.println("==========================================");
            writer.println("Report Date: " + LocalDateTime.now());
            writer.println();

            // Sensors summary
            writer.println("--- SENSORS STATE ---");
            if (sensorCRUD.getAllSensors().isEmpty()) {
                writer.println("No sensors registered.");
            } else {
                for (Sensor s : sensorCRUD.getAllSensors()) {
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
            if (applianceCRUD.getAllAppliances().isEmpty()) {
                writer.println("No appliances registered.");
            } else {
                for (Appliance app : applianceCRUD.getAllAppliances()) {
                    writer.printf("Appliance ID: %-8s | Type: %-16s | Status: %-4s | Total Consumed: %.2f kWh%n",
                            app.getApplianceID(), app.getApplianceType(), app.getStatus(), app.getEnergyConsumption());
                }
            }
            writer.println();

            // Daily log
            writer.println("--- DAILY ELECTRICITY CONSUMPTION LOG ---");
            if (dailyList.isEmpty()) {
                writer.println("No electricity consumption records found.");
            } else {
                for (int i = 0; i < dailyList.size(); i++) {
                    DailyUsage du = dailyList.elementAt(i);
                    writer.printf("Date: %s | Total Consumption: %.2f kWh%n", du.date, du.consumption);
                }
            }
            writer.println("==========================================");
            System.out.println("State report successfully saved.");
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }

        // Final snapshot of room_information.txt
        writeRoomInformation(building, "System startup complete");
    }
}
