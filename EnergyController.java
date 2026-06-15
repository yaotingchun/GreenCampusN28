public class EnergyController {

    public double calculateTotalEnergy(Building building) {
        double total = 0.0;
        for (Room room : building.getRooms()) {
            total += calculateRoomEnergy(room);
        }
        return total;
    }

    public double calculateRoomEnergy(Room room) {
        double roomTotal = 0.0;
        for (Appliance a : room.getAppliances()) {
 
            roomTotal += a.getEnergyUsage();
        }
        return roomTotal;
    } 
    
}
