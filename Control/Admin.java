package control;
import structure.Building;
import structure.Classroom;
import EnergyController;
import core.Appliance;


public class Admin {
    private String username;
    private String password;
    private Building managedBuilding;

    public Admin(String u, String p) {
        username = u;
        password = p;
    }

    public String adminGetUsername() {
        return username;
    }

    public String adminGetPassword() {
        return password;
    }

    public Room viewRooms() {
        System.out.println("\n========== BUILDING STATUS ==========");
        System.out.println("Building : " + managedBuilding.getBuildingName());

        for (Room room : managedBuilding.getRoomName()) {
            System.out.println("Room: " + room.getRoomName());
            for (Appliance a : room.getAppliances()) {
                System.out.printf("%-20s | Status: %-4s | Energy: %.2f kWh%n", a.getApplianceName());
            }
        }
    }

    public boolean loginAdmin(String u, String p) {
        return (this.username == u && this.password == p);
    }

    public void turnOnDevice() {
        Appliance a = managedBuilding.getApplianceName(roomName, applianceName);
        a.turnOn();
        System.out.println("<ADMIN> " + applianceName + " in " + roomName + " is turned ON.");
    }

    public void turnOffDevice() {
        Appliance a = managedBuilding.getApplianceName(roomName, applianceName);
        a.turnOff();
        System.out.println("<ADMIN> " + applianceName + " in " + roomName + " is turned OFF.");
    }

    public void generateReport(EnergyController e) {
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