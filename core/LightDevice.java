package core;

import java.time.LocalDateTime;
import Control.Admin;

public class LightDevice extends Appliance {
    private double brightnessLevel;
    public LightDevice(String applianceID, String status) {
        super("Light", applianceID, status, 0.0, LocalDateTime.now().toString());
        this.brightnessLevel = 0.5; // Default brightness level (50%)
    }
    public double getBrightnessLevel() {
        return brightnessLevel;
    }
    public void setBrightnessLevel(double brightnessLevel) {
        if (brightnessLevel >= 0 && brightnessLevel <= 1) {
            this.brightnessLevel = brightnessLevel;
        }
    }
    public double getEnergyUsage() {
        return brightnessLevel * 0.1; // Example energy usage based on brightness level
    }
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setBrightnessLevel(0.5); // Set to default brightness when turned on
    }
}
