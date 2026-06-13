package core;

import Control.Admin;
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

    public Appliance(String applianceType, String applianceID, String status, double energyThreshold, double energyConsumption, String lastChangeTimestamp) {
        this.applianceType = applianceType;
        this.applianceID = applianceID;
        this.status = status;
        this.energyThreshold = energyThreshold;
        this.energyConsumption = energyConsumption;
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
        return "Appliance{"
                + "applianceType='" + applianceType + '\''
                + ", applianceID='" + applianceID + '\''
                + ", status='" + status + '\''
                + ", energyThreshold=" + energyThreshold
                + ", energyConsumption=" + energyConsumption
                + ", lastChangeTimestamp='" + lastChangeTimestamp + '\''
                + ", lastChangedBy='" + lastChangedBy + '\''
                + '}';
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


