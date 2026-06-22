package core;

import structure.Building;
import structure.Area;

public class EnergyController {

    public double calculateTotalEnergy(Building building) {
        double total = 0.0;
        for (Area area : building.getAreas()) {
            total += calculateRoomEnergy(area);
        }
        return total;
    }

    public double calculateRoomEnergy(Area area) {
        double roomTotal = 0.0;
        for (Appliance a : area.getAppliances()) {
            roomTotal += a.getEnergyUsage();
        }
        return roomTotal;
    }

    public void printEnergyReport(Building building) {
        System.out.println("Energy Report for Building: " + building.getName());
        System.out.println("Total Energy Consumption: " + calculateTotalEnergy(building) + " kWh");
        for (Area area : building.getAreas()) {
            System.out.println("Area " + area.getName() + " (" + area.getAreaId() + ") Energy: " + calculateRoomEnergy(area) + " kWh");
        }
    }
}
