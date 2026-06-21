package control;

import structure.Building;
import structure.Area;
import core.Appliance;

import java.util.Scanner;
import java.util.Vector;

public class Main {
    private Building building;
    private Admin admin;
    private EnergyController energyController;
    private Vector<Admin> admins;

    public Main(Building building, Admin admin) {
        this.building = building;
        this.admin = admin;
        this.energyController = new EnergyController(building, admin);
        this.admins = new Vector<>();
        this.admins.add(admin);
    }

    public static void main(String[] args) {
        System.out.println("===================================================");
        System.out.println("   Smart Building Energy Management System       ");
        System.out.println("   Green Campus - UTM                            ");
        System.out.println("===================================================");
        System.out.println();

        Building building = new Building("Engineering Building", "123 University Ave");
        Admin admin = new Admin("admin", "admin123");

        Main app = new Main(building, admin);
        app.start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        Admin currentUser = null;

        while (currentUser == null) {
            System.out.println("--- LOGIN / REGISTER ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                currentUser = login(scanner);
            } else if (choice.equals("2")) {
                register(scanner);
                System.out.println();
            } else {
                System.out.println("Invalid option");
            }
            System.out.println();
        }

        System.out.println("Login successful!");
        System.out.println();

        admin = currentUser;

        boolean running = true;
        while (running) {
            System.out.println("--- MENU ---");
            System.out.println("1. View Building");
            System.out.println("2. View Areas");
            System.out.println("3. View Total Energy");
            System.out.println("4. Check Energy Alert");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewBuilding();
                    break;
                case "2":
                    viewAreas();
                    break;
                case "3":
                    viewTotalEnergy();
                    break;
                case "4":
                    energyController.checkThresholdAlert();
                    break;
                case "5":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option");
            }
            System.out.println();
        }

        scanner.close();
    }

    private Admin login(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (Admin a : admins) {
            if (a.authenticate(username, password)) {
                return a;
            }
        }

        System.out.println("Authentication failed!");
        return null;
    }

    private void register(Scanner scanner) {
        System.out.print("New Username: ");
        String username = scanner.nextLine();

        for (Admin a : admins) {
            if (a.getUsername().equals(username)) {
                System.out.println("Username already exists!");
                return;
            }
        }

        System.out.print("New Password: ");
        String password = scanner.nextLine();

        Admin newAdmin = new Admin(username, password);
        admins.add(newAdmin);
        System.out.println("Registration successful!");
    }

    private void viewBuilding() {
        System.out.println("--- BUILDING INFO ---");
        System.out.println("Name: " + building.getName());
        System.out.println("Address: " + building.getAddress());
        System.out.println("Areas: " + building.getAreas().size());
    }

    private void viewAreas() {
        System.out.println("--- AREAS ---");
        for (Area area : building.getAreas()) {
            System.out.println(area.toString());
        }
    }

    private void viewTotalEnergy() {
        double total = energyController.calculateTotalEnergy();
        System.out.println("--- ENERGY USAGE ---");
        System.out.printf("Total: %.2f kWh\n", total);
    }


}