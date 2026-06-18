package core;

import java.time.LocalDateTime;
import Control.Admin;

public class LightDevice extends Appliance {
    private double brightnessLevel; // 0.0 to 1.0

    public LightDevice(String applianceID, String status, double energyThreshold, double energyConsumption) {
        super("Light", applianceID, status, energyThreshold, energyConsumption, LocalDateTime.now().toString());
        this.brightnessLevel = 0.5; // Default brightness level (50%)
    }

    public double getBrightnessLevel() {
        return brightnessLevel;
    }

    public void setBrightnessLevel(double brightnessLevel) {
        if (brightnessLevel >= 0 && brightnessLevel <= 1) {
            this.brightnessLevel = brightnessLevel;
        } else {
            throw new IllegalArgumentException("Brightness must be between 0.0 and 1.0");
        }
    }

    @Override
    public double getEnergyUsage() {
        return brightnessLevel * 0.1; // Example energy usage based on brightness level
    }

    @Override
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setBrightnessLevel(0.5); // Set to default brightness when turned on
    }

    @Override
    public String toString() {
        return super.toString() +
                ", brightnessLevel=" + brightnessLevel;
    }
}