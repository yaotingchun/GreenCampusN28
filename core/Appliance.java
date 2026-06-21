package core;

import control.Admin;
import java.time.LocalDateTime;
import java.util.Vector;

public abstract class Appliance extends Device implements Controllable {

    private String status; // "ON" or "OFF"
    private double energyThreshold;
    private double energyConsumption;
    private Vector<Reading> energyUsageHistory;
    private double totalUsedTime; // in hours
    private String lastChangeTimestamp;
    private String lastChangedBy;

    public Appliance(String applianceType, String applianceID, String status,
            double energyThreshold, double energyConsumption, String lastChangeTimestamp) {
        super(applianceID, applianceType);

        // Validation
        if (status == null || (!status.equals("ON") && !status.equals("OFF"))) {
            throw new IllegalArgumentException("Status must be 'ON' or 'OFF'");
        }
        if (energyThreshold < 0) {
            throw new IllegalArgumentException("Energy threshold cannot be negative");
        }
        if (energyConsumption < 0) {
            throw new IllegalArgumentException("Energy consumption cannot be negative");
        }

        this.status = status;
        this.energyThreshold = energyThreshold;
        this.energyConsumption = energyConsumption;
        this.totalUsedTime = 0;
        this.lastChangeTimestamp = lastChangeTimestamp != null ? lastChangeTimestamp : LocalDateTime.now().toString();
        this.lastChangedBy = "NO DATA";
        this.energyUsageHistory = new Vector<>();
    }

    public void addEnergyUsage(double energyUsed, String timestamp) {
        if (energyUsed < 0) {
            throw new IllegalArgumentException("Energy usage cannot be negative");
        }
        this.energyConsumption += energyUsed;
        Reading reading = new Reading(energyUsed, timestamp);
        this.energyUsageHistory.addElement(reading);
        this.addReading(reading); // Also add to parent's history
    }

    public double getTotalUsedTime() {
        return totalUsedTime;
    }

    public void updateUsedTime(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Hours cannot be negative");
        }
        this.totalUsedTime += hours;
    }

    public String getApplianceType() {
        return getDeviceType();
    }

    public String getApplianceID() {
        return getDeviceId();
    }

    public void setApplianceID(String id) {
        setDeviceId(id);
    }

    public void setStatus(String status) {
        if (status == null || (!status.equals("ON") && !status.equals("OFF"))) {
            throw new IllegalArgumentException("Status must be 'ON' or 'OFF'");
        }
        this.status = status;
        this.lastChangeTimestamp = LocalDateTime.now().toString();
    }

    @Override
    public boolean isOn() {
        return "ON".equalsIgnoreCase(status);
    }

    @Override
    public void turnOn(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        this.status = "ON";
        this.lastChangeTimestamp = LocalDateTime.now().toString();
        this.lastChangedBy = admin.getUsername();
    }

    @Override
    public void turnOff(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        this.status = "OFF";
        this.lastChangeTimestamp = LocalDateTime.now().toString();
        this.lastChangedBy = admin.getUsername();
    }

    @Override
    public String getStatus() {
        return status;
    }

    public double getEnergyThreshold() {
        return energyThreshold;
    }

    public void setEnergyThreshold(double energyThreshold) {
        if (energyThreshold < 0) {
            throw new IllegalArgumentException("Energy threshold cannot be negative");
        }
        this.energyThreshold = energyThreshold;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        if (energyConsumption < 0) {
            throw new IllegalArgumentException("Energy consumption cannot be negative");
        }
        this.energyConsumption = energyConsumption;
    }

    public abstract double getEnergyUsage();

    public void reset() {
        this.energyConsumption = 0;
        this.totalUsedTime = 0;
        this.energyUsageHistory.clear();
        this.clearHistory();
    }

    public String getLastChangeTimestamp() {
        return lastChangeTimestamp;
    }

    public String getLastChangedBy() {
        return lastChangedBy;
    }

    @Override
    public String toString() {
        return "Appliance{" +
                "applianceType='" + getApplianceType() + '\'' +
                ", applianceID='" + getApplianceID() + '\'' +
                ", status='" + status + '\'' +
                ", energyThreshold=" + energyThreshold +
                ", energyConsumption=" + energyConsumption +
                ", totalUsedTime=" + totalUsedTime +
                ", lastChangeTimestamp='" + lastChangeTimestamp + '\'' +
                ", lastChangedBy='" + lastChangedBy + '\'' +
                '}';
    }

    public void displayEnergyUsageHistory() {
        System.out.println("Energy Usage History for " + getApplianceID() + ":");
        for (Reading reading : energyUsageHistory) {
            System.out.println("  " + reading);
        }
    }

    public Vector<Reading> getEnergyUsageHistory() {
        return new Vector<>(energyUsageHistory); // Return a copy for encapsulation
    }
}