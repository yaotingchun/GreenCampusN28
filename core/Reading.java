package core;
public class Reading {
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
