package core;

public class ElectricitySensor extends Sensor {
    public ElectricitySensor(String sensorID, double value, String timestamp) {
        super("Electricity", sensorID, value, timestamp, "kWh");
    }
}