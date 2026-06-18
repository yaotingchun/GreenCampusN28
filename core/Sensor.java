package core;

import java.util.Vector;

public abstract class Sensor extends Device {
    private String unit;
    private Vector<Reading> sensorReadingHistory;

    public Sensor(String sensorType, String sensorID, double value, String timestamp, String unit) {
        super(sensorID, sensorType);

        // Validation
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit cannot be null or empty");
        }
        if (timestamp == null || timestamp.trim().isEmpty()) {
            throw new IllegalArgumentException("Timestamp cannot be null or empty");
        }

        this.unit = unit;
        this.sensorReadingHistory = new Vector<>();

        // Add initial reading
        addReading(value, timestamp);
    }

    public String getSensorType() {
        return getDeviceType();
    }

    public void setSensorType(String sensorType) {
        setDeviceType(sensorType);
    }

    public String getSensorID() {
        return getDeviceId();
    }

    public void setSensorID(String sensorID) {
        setDeviceId(sensorID);
    }

    public void displayReadingHistory() {
        System.out.println("Reading History for " + getSensorID() + ":");
        for (Reading reading : sensorReadingHistory) {
            System.out.println("  " + reading);
        }
    }

    public void addReading(double value, String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            throw new IllegalArgumentException("Timestamp cannot be null or empty");
        }
        Reading reading = new Reading(value, timestamp);
        this.sensorReadingHistory.addElement(reading);
        this.addReading(reading); // Also add to parent's history
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
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit cannot be null or empty");
        }
        this.unit = unit;
    }

    public Vector<Reading> getSensorReadingHistory() {
        return new Vector<>(sensorReadingHistory); // Return a copy for encapsulation
    }

    public void clearSensorHistory() {
        sensorReadingHistory.clear();
        this.clearHistory();
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorType='" + getSensorType() + '\'' +
                ", sensorID='" + getSensorID() + '\'' +
                ", unit='" + unit + '\'' +
                ", latestReading=" + getLatestReading() +
                ", historySize=" + sensorReadingHistory.size() +
                '}';
    }
}