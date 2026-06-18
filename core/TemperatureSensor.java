package core;

public class TemperatureSensor extends Sensor {
    public TemperatureSensor(String sensorID, double value, String timestamp) {
        super("Temperature", sensorID, value, timestamp, "°C");
    }
}