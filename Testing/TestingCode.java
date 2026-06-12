package Testing;
import java.time.LocalDateTime;
import java.util.Vector;
import java.io.*;

public class TestingCode {
    public static void main(String[] args) {
        Admin admin = new Admin("admin", "admin123");
        SensorCRUD sensorCRUD = new SensorCRUD();
        ApplianceCRUD applianceCRUD = new ApplianceCRUD();

        // 1. Read sensor values from txt input file
        File inputFile = new File("sensor_input.txt");
        if (inputFile.exists()) {
            System.out.println("Reading sensor data from " + inputFile.getName() + "...");
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length < 4) continue;
                    String type = parts[0].trim();
                    String id = parts[1].trim();
                    double value = Double.parseDouble(parts[2].trim());
                    String timestamp = parts[3].trim();

                    Sensor existing = sensorCRUD.getSensorBySensorID(id);
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

        // 2. Register mock appliances and simulate daily electricity usage
        LightDevice light = new LightDevice("LGT01", "OFF");
        AirConditioner ac = new AirConditioner("AC01", "OFF");
        Fan fan = new Fan("FAN01", "OFF");

        applianceCRUD.register(light);
        applianceCRUD.register(ac);
        applianceCRUD.register(fan);

        light.turnOn(admin);
        ac.turnOn(admin);
        fan.turnOn(admin);

        // Simulate daily electricity consumption logs
        light.addEnergyUsage(0.8, "2026-06-11T09:00:00");
        light.addEnergyUsage(1.2, "2026-06-11T15:00:00");
        light.addEnergyUsage(1.0, "2026-06-12T08:00:00");

        ac.addEnergyUsage(4.5, "2026-06-10T12:00:00");
        ac.addEnergyUsage(5.0, "2026-06-11T14:00:00");
        ac.addEnergyUsage(4.8, "2026-06-12T13:00:00");

        fan.addEnergyUsage(1.5, "2026-06-11T10:00:00");
        fan.addEnergyUsage(1.8, "2026-06-12T11:00:00");

        // 3. Aggregate daily electricity consumption without using Map
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
                String date = (timestamp != null && timestamp.length() >= 10) ? timestamp.substring(0, 10) : "Unknown Date";
                
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

        // Sort the daily list chronologically by date using standard bubble sort
        for (int i = 0; i < dailyList.size() - 1; i++) {
            for (int j = 0; j < dailyList.size() - i - 1; j++) {
                if (dailyList.elementAt(j).date.compareTo(dailyList.elementAt(j + 1).date) > 0) {
                    DailyUsage temp = dailyList.elementAt(j);
                    dailyList.setElementAt(dailyList.elementAt(j + 1), j);
                    dailyList.setElementAt(temp, j + 1);
                }
            }
        }

        // 4. Output system state to system_state.txt
        File outputFile = new File("system_state.txt");
        System.out.println("Saving current state to " + outputFile.getName() + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("==========================================");
            writer.println("         GREEN CAMPUS SYSTEM STATE        ");
            writer.println("==========================================");
            writer.println("Report Date: " + LocalDateTime.now());
            writer.println();

            writer.println("--- SENSORS STATE ---");
            if (sensorCRUD.getAllSensors().isEmpty()) {
                writer.println("No sensors registered.");
            } else {
                for (Sensor s : sensorCRUD.getAllSensors()) {
                    Reading latest = s.getLatestReading();
                    String valStr = (latest != null) ? (latest.getValue() + " " + s.getUnit()) : "No readings";
                    String timeStr = (latest != null) ? latest.getTimestamp() : "N/A";
                    writer.printf("Sensor ID: %-8s | Type: %-12s | Latest: %-12s | Time: %s%n", 
                        s.getSensorID(), s.getSensorType(), valStr, timeStr);
                }
            }
            writer.println();

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
    }
}

class Reading {
    private double value;
    private String timestamp;
    public Reading(double value, String timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String toString() {
        return timestamp + ": " + value;
    }
}

abstract class Sensor {
    private String sensorType;
    private String sensorID;
    private Vector<Reading> sensorReadingHistory;
    private String unit;
    public Sensor(String sensorType, String sensorID, double value, String timestamp, String unit) {
        this.sensorType = sensorType;
        this.sensorID = sensorID;
        this.sensorReadingHistory = new Vector<>();
        this.sensorReadingHistory.addElement(new Reading(value, timestamp));
        this.unit = unit;
    }
    public String getSensorType() {
        return sensorType;
    }
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }
    public String getSensorID() {
        return sensorID;
    }
    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }
    public void displayReadingHistory() {
        for (Reading reading : sensorReadingHistory) {
            System.out.println("Value: " + reading.getValue() + " at " + reading.getTimestamp());
        }
    }
    public void addReading(double value, String timestamp) {
        this.sensorReadingHistory.addElement(new Reading(value, timestamp));
    }
    public Reading getLatestReading() {
        if (!sensorReadingHistory.isEmpty()) {
            return sensorReadingHistory.lastElement();
        }
        return null;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}

class LightSensor extends Sensor {
    public LightSensor(String sensorID, double value, String timestamp) {
        super("Light", sensorID, value, timestamp, "lux");
    }
}

class TemperatureSensor extends Sensor {
    public TemperatureSensor(String sensorID, double value, String timestamp) {
        super("Temperature", sensorID, value, timestamp, "°C");
    }
}

class ElectricitySensor extends Sensor {
    public ElectricitySensor(String sensorID, double value, String timestamp) {
        super("Electricity", sensorID, value, timestamp, "kWh");
    }
}

class SensorCRUD {
    private Vector<Sensor> sensors;
    public SensorCRUD() {
        sensors = new Vector<>();
    }
    public Vector<Sensor> getAllSensors() {
        return sensors;
    }
    public void addSensor(Sensor sensor) {
        sensors.addElement(sensor);
    }
    public void removeSensor(Sensor sensor) {
        sensors.removeElement(sensor);
    }
    public Sensor getSensorBySensorID(String sensorID) {
        for (Sensor sensor : sensors) {
            if (sensor.getSensorID().equals(sensorID)) {
                return sensor;
            }
        }
        return null;
    }
    public void displayAllSensors() {
        for (Sensor sensor : sensors) {
            System.out.println(sensor);
        }
    }
    public void displaySensorsByType(String sensorType) {
        for (Sensor sensor : sensors) {
            if (sensor.getSensorType().equals(sensorType)) {
                System.out.println(sensor);
            }
        }
    }
}

abstract class Appliance implements Controllable {
    private String applianceType;
    private String applianceID;
    private String status; // "ON" or "OFF"
    private double energyThreshold;
    private double energyConsumption;
    private Vector<Reading> energyUsageHistory;
    private double totalUsedTime; // in hours
    private String lastChangeTimestamp;
    private String lastChangedBy;

    public Appliance(String applianceType, String applianceID, String status, double energyThreshold, String lastChangeTimestamp) {
        this.applianceType = applianceType;
        this.applianceID = applianceID;
        this.status = status;
        this.energyThreshold = energyThreshold;
        this.energyConsumption = 0;
        this.totalUsedTime = 0;
        this.lastChangeTimestamp = LocalDateTime.now().toString();
        this.lastChangedBy = "NO DATA";
        this.energyUsageHistory = new Vector<>();
    }
    public void addEnergyUsage(double energyUsed, String timestamp) {
        this.energyConsumption += energyUsed;
        this.energyUsageHistory.addElement(new Reading(energyUsed, timestamp));
    }
    public double getTotalUsedTime() {
        return totalUsedTime;
    }
    public void updateUsedTime(double hours) {
        this.totalUsedTime += hours;
    }
    public String getApplianceType() {
        return applianceType;
    }
    public String getApplianceID() {
        return applianceID;
    }
    public void setApplianceID(String id) {
        this.applianceID = id;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOn() {
        return "ON".equalsIgnoreCase(status);
    }
    public void turnOn(Admin admin) { 
        this.status = "ON";
        this.lastChangeTimestamp = LocalDateTime.now().toString();
        this.lastChangedBy = admin.getUsername();
    }
    public void turnOff(Admin admin) {
        this.status = "OFF";
        this.lastChangeTimestamp = LocalDateTime.now().toString();
        this.lastChangedBy = admin.getUsername();
    }
    public String getStatus() {
        return status;
    }
    
    public double getEnergyThreshold() {
        return energyThreshold;
    }
    public void setEnergyThreshold(double energyThreshold) {
        this.energyThreshold = energyThreshold;
    }
    public double getEnergyConsumption() {
        return energyConsumption;
    }
    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
    public abstract double getEnergyUsage();

    public void reset() {
        this.energyConsumption = 0;
        this.totalUsedTime = 0;
        this.energyUsageHistory.clear();
    }
    public String toString() {
        return "Appliance{" +
                "applianceType='" + applianceType + '\'' +
                ", applianceID='" + applianceID + '\'' +
                ", status='" + status + '\'' +
                ", energyThreshold=" + energyThreshold +
                ", energyConsumption=" + energyConsumption +
                ", lastChangeTimestamp='" + lastChangeTimestamp + '\'' +
                ", lastChangedBy='" + lastChangedBy + '\'' +
                '}';
        }
    public void displayEnergyUsageHistory() {
        System.out.println("Energy Usage History for " + applianceID + ":");
        for (Reading reading : energyUsageHistory) {
            System.out.println("Value: " + reading.getValue() + " at " + reading.getTimestamp());
        }
    }
    public Vector<Reading> getEnergyUsageHistory() {
        return energyUsageHistory;
    }
}

class LightDevice extends Appliance {
    private double brightnessLevel;
    public LightDevice(String applianceID, String status) {
        super("Light", applianceID, status, 0.0, LocalDateTime.now().toString());
        this.brightnessLevel = 0.5; // Default brightness level (50%)
    }
    public double getBrightnessLevel() {
        return brightnessLevel;
    }
    public void setBrightnessLevel(double brightnessLevel) {
        if (brightnessLevel >= 0 && brightnessLevel <= 1) {
            this.brightnessLevel = brightnessLevel;
        }
    }

    public double getEnergyUsage() {
        return brightnessLevel * 0.1; // Example energy usage based on brightness level
    }
}

class AirConditioner extends Appliance {
    private double temperatureSetting;
    public AirConditioner(String applianceID, String status) {
        super("Air Conditioner", applianceID, status,  0.0, LocalDateTime.now().toString());
    }
    public double getEnergyUsage() {
        // Example energy usage based on temperature setting
        return Math.abs(temperatureSetting - 20) * 0.5;
    }
}

class Fan extends Appliance {
    private int speedLevel; // 1 to 5
    public Fan(String applianceID, String status) {
        super("Fan", applianceID, status, 0.0, LocalDateTime.now().toString());
        this.speedLevel = 3; // Default speed level
    }
    public int getSpeedLevel() {
        return speedLevel;
    }
    public void setSpeedLevel(int speedLevel) {
        if (speedLevel >= 1 && speedLevel <= 5) {
            this.speedLevel = speedLevel;
        }
    }
    public double getEnergyUsage() {
        return speedLevel * 0.5; // Example energy usage based on speed level
    }

}

class ApplianceCRUD {
    private Vector<Appliance> appliances;
    public ApplianceCRUD() {
        appliances = new Vector<>();
    }
    public void register(Appliance appliance) {
        appliances.addElement(appliance);
    }
    public boolean remove(String applianceID) {
        for (Appliance appliance : appliances) {
            if (appliance.getApplianceID().equals(applianceID)) {
                appliances.removeElement(appliance);
                return true;
            }
        }
        return false;
    }
    public Appliance getAppliance(String applianceID) {
        for (Appliance appliance : appliances) {
            if (appliance.getApplianceID().equals(applianceID)) {
                return appliance;
            }
        }
        return null;
    }
    public Vector<Appliance> getAllAppliances() {
        return appliances;
    }
    public void displayAppliancesByType(String applianceType) {
        for (Appliance appliance : appliances) {
            if (appliance.getApplianceType().equals(applianceType)) {
                System.out.println(appliance);
            }
        }
    }
    public Vector<Appliance> getAppliancesByStatus(String status) {
        Vector<Appliance> result = new Vector<>();
        for (Appliance appliance : appliances) {
            if (appliance.getStatus().equals(status)) {
                result.addElement(appliance);
            }
        }
        return result;
    }
    public void displayAlert(){
        for (Appliance appliance : appliances) {
            if (appliance.getEnergyConsumption() > appliance.getEnergyThreshold()) {
                System.out.println("ALERT: " + appliance.getApplianceID() + " has exceeded its energy threshold!");
            }
        }
    }
}

interface Controllable {
    void turnOn(Admin admin);
    void turnOff(Admin admin);
    boolean isOn();
    String getStatus();
}

class Building {}

class Area {}

class Classroom extends Area {}

class Corridor extends Area {}

class Office extends Area {}

class Toilet extends Area {}

class LectureHall extends Area {}

class StudentLounge extends Area {}

class Admin {
    private String username;
    private String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}

