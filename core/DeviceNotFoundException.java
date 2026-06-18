package core;

public class DeviceNotFoundException extends Exception {
    private String deviceId;
    private String deviceType;

    public DeviceNotFoundException() {
        super("Device not found in the system.");
    }

    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(String deviceId, String deviceType) {
        super(deviceType + " with ID '" + deviceId + "' was not found in the system.");
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public DeviceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }
}