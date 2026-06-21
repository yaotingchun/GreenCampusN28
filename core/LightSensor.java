package core;

public class LightSensor extends Sensor {
    public LightSensor(String sensorID, double value, String timestamp) {
        super("Light Sensor", sensorID, value, timestamp, "Lux");
    }
}