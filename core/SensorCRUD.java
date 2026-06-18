package core;

import java.util.Vector;

public class SensorCRUD {
    private Vector<Sensor> sensors;

    public SensorCRUD() {
        sensors = new Vector<>();
    }

    public void addSensor(Sensor sensor) {
        sensors.addElement(sensor);
    }

    public void removeSensor(Sensor sensor) {
        sensors.removeElement(sensor);
    }

    public Sensor getSensorBySerialNumber(String serialNumber) {
        for (Sensor sensor : sensors) {
            if (sensor.getSensorID().equals(serialNumber)) {
                return sensor;
            }
        }
        return null; // Return null instead of throwing exception
    }

    public Vector<Sensor> getAllSensors() {
        return sensors;
    }

    public void displayAllSensors() {
        for (Sensor sensor : sensors) {
            System.out.println(sensor);
        }
    }

    public void displaySensorsByType(String sensorType) {
        for (Sensor sensor : sensors) {
            if (sensor.getSensorType().equals(sensorType)) {
                System.out.println(sensor);
            }
        }
    }

    public boolean updateSensor(String sensorId, double newValue, String timestamp) {
        Sensor target = getSensorBySerialNumber(sensorId);
        if (target != null) {
            target.addReading(newValue, timestamp);
            System.out.println("Sensor " + sensorId + " updated: value=" + newValue + " at " + timestamp);
            return true;
        }
        System.out.println("[WARN] Sensor " + sensorId + " not found. Update skipped.");
        return false;
    }

    public boolean exists(String sensorId) {
        return getSensorBySerialNumber(sensorId) != null;
    }
}