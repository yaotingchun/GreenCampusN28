package structure;

import core.Appliance;
import core.Reading;
import core.Sensor;
import java.util.Vector;

public class Area {
    private String areaId;
    private String name;
    private Vector<Sensor> sensors;
    private Vector<Appliance> appliances;

    public Area(String areaId, String name) {
        this.areaId = areaId;
        this.name = name;
        sensors = new Vector<>();
        appliances = new Vector<>();
    }

    // --- Getters ---
    public String getAreaId() {
        return areaId;
    }

    /** Alias for getAreaId() — used for backward compatibility. */
    public String getRoomId() {
        return areaId;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the concrete subclass name as the area type.
     * e.g. "Classroom", "LectureHall", "Office", "Corridor"
     */
    public String getAreaType() {
        return this.getClass().getSimpleName();
    }

    // --- Sensor operations ---
    public void addSensor(Sensor sensor) {
        sensors.addElement(sensor);
    }

    public void removeSensor(Sensor sensor) {
        sensors.removeElement(sensor);
    }

    /** Get the sensor list (also available as getSensors()). */
    public Vector<Sensor> getSensorList() {
        return sensors;
    }

    /** Alias for getSensorList(). */
    public Vector<Sensor> getSensors() {
        return sensors;
    }

    // --- Appliance operations ---
    public void addAppliance(Appliance appliance) {
        appliances.addElement(appliance);
    }

    public void removeAppliance(Appliance appliance) {
        appliances.removeElement(appliance);
    }

    /** Get the appliance list (also available as getAppliances()). */
    public Vector<Appliance> getApplianceList() {
        return appliances;
    }

    /** Alias for getApplianceList(). */
    public Vector<Appliance> getAppliances() {
        return appliances;
    }

    @Override
    public String toString() {
        return areaId + " - " + name + " [" + getAreaType() + "]";
    }

    /**
     * Returns a detailed multi-line summary of this area including
     * all sensors and appliances.
     */
    public String toDetailedString() {
        String result = "";

        result += "  Area ID   : " + areaId + "\n";
        result += "  Name      : " + name + "\n";
        result += "  Type      : " + getAreaType() + "\n";

        result += "  Sensors (" + sensors.size() + "):\n";
        if (sensors.isEmpty()) {
            result += "    (none)\n";
        } else {
            for (Sensor s : sensors) {
                Reading latest = s.getLatestReading();
                String valStr = (latest != null)
                        ? latest.getValue() + " " + s.getUnit()
                        : "No readings";
                String timeStr = (latest != null)
                        ? latest.getTimestamp()
                        : "N/A";

                result += String.format(
                        "    [Sensor]    ID: %-8s | Type: %-14s | Latest: %-12s | Time: %s%n",
                        s.getSensorID(),
                        s.getSensorType(),
                        valStr,
                        timeStr);
            }
        }

        result += "  Appliances (" + appliances.size() + "):\n";
        if (appliances.isEmpty()) {
            result += "    (none)\n";
        } else {
            for (Appliance a : appliances) {
                result += String.format(
                        "    [Appliance] ID: %-8s | Type: %-16s | Status: %-4s | Consumed: %.2f kWh%n",
                        a.getApplianceID(),
                        a.getApplianceType(),
                        a.getStatus(),
                        a.getEnergyConsumption());
            }
        }

        return result;
    }
}