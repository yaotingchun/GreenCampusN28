package core;

import java.time.LocalDateTime;
import Control.Admin;

public class Fan extends Appliance {
    private int speedLevel; // 1 to 5
    public Fan(String applianceID, String status, double energyThreshold, double energyConsumption) {
        super("Fan", applianceID, status, energyThreshold, energyConsumption, LocalDateTime.now().toString());
        this.speedLevel = 3; // Default speed level
    }
    public int getSpeedLevel() {
        return speedLevel;
    }
    public void setSpeedLevel(int speedLevel) {
        if (speedLevel >= 1 && speedLevel <= 5) {
            this.speedLevel = speedLevel;
        }
    }
    public double getEnergyUsage() {
        return speedLevel * 0.5; // Example energy usage based on speed level
    }
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setSpeedLevel(3); // Set to default speed when turned on
    }
}
