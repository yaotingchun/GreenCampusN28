package core;

import java.util.Vector;

public class ApplianceCRUD {
    private Vector<Appliance> appliances;

    public ApplianceCRUD() {
        appliances = new Vector<>();
    }

    public void register(Appliance appliance) {
        appliances.addElement(appliance);
    }

    public boolean remove(String applianceID) {
        for (Appliance appliance : appliances) {
            if (appliance.getApplianceID().equals(applianceID)) {
                appliances.removeElement(appliance);
                return true;
            }
        }
        return false;
    }

    public Appliance getAppliance(String applianceID) {
        for (Appliance appliance : appliances) {
            if (appliance.getApplianceID().equals(applianceID)) {
                return appliance;
            }
        }
        return null; // Return null instead of throwing exception
    }

    public Vector<Appliance> getAllAppliances() {
        return appliances;
    }

    public void displayAppliancesByType(String applianceType) {
        for (Appliance appliance : appliances) {
            if (appliance.getApplianceType().equals(applianceType)) {
                System.out.println(appliance);
            }
        }
    }

    public Vector<Appliance> getAppliancesByStatus(String status) {
        Vector<Appliance> result = new Vector<>();
        for (Appliance appliance : appliances) {
            if (appliance.getStatus().equals(status)) {
                result.addElement(appliance);
            }
        }
        return result;
    }

    public void displayAlert() {
        for (Appliance appliance : appliances) {
            if (appliance.getEnergyConsumption() > appliance.getEnergyThreshold()) {
                System.out.println("ALERT: " + appliance.getApplianceID() + " has exceeded its energy threshold!");
            }
        }
    }

    public boolean updateAppliance(String applianceId, String newStatus, double newThreshold) {
        Appliance target = getAppliance(applianceId);
        if (target != null) {
            if (newStatus != null && !newStatus.isEmpty()) {
                target.setStatus(newStatus);
                System.out.println("Appliance " + applianceId + " status updated to: " + newStatus);
            }
            if (newThreshold >= 0) {
                target.setEnergyThreshold(newThreshold);
                System.out
                        .println("Appliance " + applianceId + " energy threshold updated to: " + newThreshold + " kWh");
            }
            return true;
        }
        System.out.println("[WARN] Appliance " + applianceId + " not found. Update skipped.");
        return false;
    }

    public boolean exists(String applianceId) {
        return getAppliance(applianceId) != null;
    }
}