package core;

import java.time.LocalDateTime;
import Control.Admin;

public class AirConditioner extends Appliance {
    private double temperatureSetting;
    public AirConditioner(String applianceID, String status) {
        super("Air Conditioner", applianceID, status,  0.0, LocalDateTime.now().toString());
    }
    public double getEnergyUsage() {
        // Example energy usage based on temperature setting
        return Math.abs(temperatureSetting - 20) * 0.5;
    }
    public double getTemperatureSetting() {
        return temperatureSetting;
    }
    public void setTemperatureSetting(double temperatureSetting) {
        if (temperatureSetting >= 16 && temperatureSetting <= 30) {
            this.temperatureSetting = temperatureSetting;
        }
    }
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setTemperatureSetting(24); // Default temperature setting when turned on
    }
}
