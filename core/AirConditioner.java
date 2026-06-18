package core;

import java.time.LocalDateTime;
import Control.Admin;

public class AirConditioner extends Appliance {
    private double temperatureSetting;

    public AirConditioner(String applianceID, String status, double energyThreshold, double energyConsumption) {
        super("Air Conditioner", applianceID, status, energyThreshold, energyConsumption,
                LocalDateTime.now().toString());
        this.temperatureSetting = 24; // Default temperature
    }

    @Override
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
        } else {
            throw new IllegalArgumentException("Temperature must be between 16°C and 30°C");
        }
    }

    @Override
    public void turnOn(Admin admin) {
        super.turnOn(admin);
        setTemperatureSetting(24); // Default temperature setting when turned on
    }

    @Override
    public String toString() {
        return super.toString() +
                ", temperatureSetting=" + temperatureSetting;
    }
}