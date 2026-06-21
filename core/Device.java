package core;

import java.util.Vector;

public abstract class Device {
    private String deviceId;
    private String deviceType;
    private Vector<Reading> history;

    public Device(String deviceId, String deviceType) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Device ID cannot be null or empty");
        }
        if (deviceType == null || deviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Device type cannot be null or empty");
        }
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.history = new Vector<>();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Vector<Reading> getHistory() {
        return history;
    }

    public void addReading(Reading reading) {
        if (reading != null) {
            history.addElement(reading);
        }
    }

    public Reading getLatestReading() {
        if (!history.isEmpty()) {
            return history.lastElement();
        }
        return null;
    }

    public void clearHistory() {
        history.clear();
    }

    public void displayHistory() {
        System.out.println("History for " + deviceId + " (" + deviceType + "):");
        for (Reading reading : history) {
            System.out.println("  " + reading);
        }
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", historySize=" + history.size() +
                '}';
    }
}
