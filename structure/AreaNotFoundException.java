package structure;

public class AreaNotFoundException extends Exception {

    private String areaId;
    private String buildingName;

    public AreaNotFoundException() {
        super("Area not found in the building.");
    }

    public AreaNotFoundException(String areaId) {
        super("Area with ID '" + areaId + "' was not found.");
        this.areaId = areaId;
    }

    public AreaNotFoundException(String areaId, String buildingName) {
        super("Area with ID '" + areaId + "' was not found in building '" + buildingName + "'.");
        this.areaId = areaId;
        this.buildingName = buildingName;
    }

    public AreaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getAreaId() {
        return areaId;
    }

    public String getBuildingName() {
        return buildingName;
    }
}