package structure;

import java.util.ArrayList;

import core.Appliance;
import core.Sensor;
import Control.GreenCampus;

public class Building {
    private String name;
    private String address;
    private ArrayList<Area> areas;

    public Building(String name, String address) {
        this.name = name;
        this.address = address;
        areas = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    // ===== AREA =====
    public void addArea(Area area) {
        areas.add(area);
    }

    public void addArea(String areaId, String name) {
        Area area = new Area(areaId, name);
        areas.add(area);
    }

    public ArrayList<Area> getAreas() {
        return areas;
    }

    // ===== SENSOR =====
    public void addSensorToArea(Area area, Sensor sensor) {
        area.addSensor(sensor);

        GreenCampus.writeRoomInformation(
                this,
                "addSensor: " + sensor.getSensorID() +
                        " -> area " + area.getAreaId());
    }

    public void removeSensorFromArea(Area area, Sensor sensor) {
        area.removeSensor(sensor);

        GreenCampus.writeRoomInformation(
                this,
                "removeSensor: " + sensor.getSensorID() +
                        " -> area " + area.getAreaId());
    }

    // ===== APPLIANCE =====
    public void addApplianceToArea(Area area, Appliance appliance) {
        area.addAppliance(appliance);

        GreenCampus.writeRoomInformation(
                this,
                "addAppliance: " + appliance.getApplianceID() +
                        " -> area " + area.getAreaId());
    }

    public void removeApplianceFromArea(Area area, Appliance appliance) {
        area.removeAppliance(appliance);

        GreenCampus.writeRoomInformation(
                this,
                "removeAppliance: " + appliance.getApplianceID() +
                        " -> area " + area.getAreaId());
    }

    // ===== DISPLAY =====
    public void listAllAreas() {
        for (Area area : areas) {
            System.out.println(area.toString());
        }
    }

    @Override
    public String toString() {
        return "Building: " + name +
                " | Address: " + address +
                " | Areas: " + areas.size();
    }
}