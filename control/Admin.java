package control;

import structure.Building;
import structure.Area;
import core.EnergyController;
import core.Appliance;

public class Admin {
    private String username;
    private String password;
    private double energyThreshold;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        this.energyThreshold = 100.0;
    }

    public boolean authenticate(String inputUser, String inputPass) {
        return this.username.equals(inputUser) && this.password.equals(inputPass);
    }

    public String getUsername() {
        return username;
    }

    public double getEnergyThreshold() {
        return energyThreshold;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setManagedBuilding(Building building) {
        this.managedBuilding = building;
    }

    public Building getManagedBuilding() {
        return managedBuilding;
    }

    public void viewRooms() {
        if (managedBuilding == null) {
            System.out.println("No building managed by this admin.");
            return;
        }
        System.out.println("\n========== BUILDING STATUS ==========");
        System.out.println("Building : " + managedBuilding.getName());

        for (Area area : managedBuilding.getAreas()) {
            System.out.println("Room: " + area.getName());
            for (Appliance a : area.getAppliances()) {
                System.out.printf("%-20s | Status: %-4s | Energy: %.2f kWh%n",
                        a.getApplianceID(), a.getStatus(), a.getEnergyConsumption());
            }
        }
    }

    public boolean loginAdmin(String u, String p) {
        return (this.username.equals(u) && this.password.equals(p));
    }

    public void turnOnDevice(String areaId, String applianceId) {
        if (managedBuilding == null) {
            System.out.println("No building managed by this admin.");
            return;
        }
        for (Area area : managedBuilding.getAreas()) {
            if (area.getAreaId().equals(areaId)) {
                for (Appliance a : area.getAppliances()) {
                    if (a.getApplianceID().equals(applianceId)) {
                        a.turnOn(this);
                        System.out.println("<ADMIN> " + applianceId + " in " + areaId + " is turned ON.");
                        return;
                    }
                }
            }
        }
        System.out.println("Appliance " + applianceId + " in area " + areaId + " not found.");
    }

    public void turnOffDevice(String areaId, String applianceId) {
        if (managedBuilding == null) {
            System.out.println("No building managed by this admin.");
            return;
        }
        for (Area area : managedBuilding.getAreas()) {
            if (area.getAreaId().equals(areaId)) {
                for (Appliance a : area.getAppliances()) {
                    if (a.getApplianceID().equals(applianceId)) {
                        a.turnOff(this);
                        System.out.println("<ADMIN> " + applianceId + " in " + areaId + " is turned OFF.");
                        return;
                    }
                }
            }
        }
        System.out.println("Appliance " + applianceId + " in area " + areaId + " not found.");
    }

    public void generateReport(EnergyController e) {
        if (managedBuilding == null) {
            System.out.println("No building managed by this admin.");
            return;
        }
        System.out.println("\n===== ENERGY USAGE SUMMARY =====");
        System.out.println("Admin    : " + username + "  (ID: " + password + ")");
        e.printEnergyReport(managedBuilding);
        System.out.println("================================\n");
    }

    @Override
    public String toString() {
        return ("------------ SYSTEMS REPORT ------------");
    }
}