package control;

import structure.Building;
import structure.Area;
import core.Appliance;

import java.util.Vector;

public class EnergyController {
    private Building building;
    private Admin admin;

    public EnergyController(Building building, Admin admin) {
        this.building = building;
        this.admin = admin;
    }

    public double calculateTotalEnergy() {
        double total = 0.0;
        for (Area area : building.getAreas()) {
            for (Appliance appliance : area.getAppliances()) {
                total += appliance.getEnergyUsage();
            }
        }
        return total;
    }

    public void checkThresholdAlert() {
        double total = calculateTotalEnergy();
        if (total > admin.getEnergyThreshold()) {
            System.out.println("\n========================================");
            System.out.println("  [ALERT] THRESHOLD EXCEEDED");
            System.out.printf("  Current Usage: %.2f kWh\n", total);
            System.out.printf("  Limit: %.2f kWh\n", admin.getEnergyThreshold());
            System.out.println("========================================\n");
        } else {
            System.out.println("Energy is within threshold.\n");
        }
    }

    public void displayConsumptionByType() {
        System.out.println("--- Energy Consumption by Type ---");
        Vector<String> types = new Vector<>();
        
        for (Area area : building.getAreas()) {
            for (Appliance app : area.getAppliances()) {
                String type = app.getApplianceType();
                if (!types.contains(type)) {
                    types.add(type);
                }
            }
        }

        for (String type : types) {
            double sum = 0.0;
            for (Area area : building.getAreas()) {
                for (Appliance app : area.getAppliances()) {
                    if (app.getApplianceType().equals(type)) {
                        sum += app.getEnergyUsage();
                    }
                }
            }
            System.out.printf("%-16s: %.2f kWh\n", type, sum);
        }
    }

    public void displayConsumptionByArea() {
        System.out.println("--- Energy Consumption by Area ---");
        for (Area area : building.getAreas()) {
            double sum = 0.0;
            for (Appliance app : area.getAppliances()) {
                sum += app.getEnergyUsage();
            }
            System.out.printf("%-20s: %.2f kWh\n", area.getName(), sum);
        }
    }
}
