package control;

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

    public void setEnergyThreshold(double energyThreshold) {
        this.energyThreshold = energyThreshold;
    }
}