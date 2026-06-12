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
            if (sensor.getSerialNumber().equals(serialNumber)) {
                return sensor;
            }
        }
        return null;
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
}
