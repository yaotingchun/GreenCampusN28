package core;
import java.util.Vector;

public abstract class Sensor {
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

