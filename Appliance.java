import java.time.LocalDateTime;
import java.util.Vector;

public abstract class Appliance implements Controllable {
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
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setBrightnessLevel(0.5); // Set to default brightness when turned on
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
    public double getTemperatureSetting() {
        return temperatureSetting;
    }
    public void setTemperatureSetting(double temperatureSetting) {
        if (temperatureSetting >= 16 && temperatureSetting <= 30) {
            this.temperatureSetting = temperatureSetting;
        }
    }
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setTemperatureSetting(24); // Default temperature setting when turned on
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
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setSpeedLevel(3); // Set to default speed when turned on
    }
}