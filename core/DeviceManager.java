package core;

import java.util.Vector;

public class DeviceManager<T extends Device> {
    private Vector<T> devices;

    public DeviceManager() {
        this.devices = new Vector<>();
    }

    public void register(T device) {
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null");
        }
        devices.addElement(device);
    }

    public T getDevice(String deviceId) throws DeviceNotFoundException {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Device ID cannot be null or empty");
        }

        for (T device : devices) {
            if (device.getDeviceId().equals(deviceId)) {
                return device;
            }
        }
        throw new DeviceNotFoundException(deviceId, "Device");
    }

    public boolean remove(String deviceId) throws DeviceNotFoundException {
        T device = getDevice(deviceId);
        return devices.removeElement(device);
    }

    public Vector<T> getAllDevices() {
        return new Vector<>(devices);
    }

    public Vector<T> getDevicesByType(String deviceType) {
        Vector<T> result = new Vector<>();
        for (T device : devices) {
            if (device.getDeviceType().equalsIgnoreCase(deviceType)) {
                result.addElement(device);
            }
        }
        return result;
    }

    public boolean exists(String deviceId) {
        try {
            getDevice(deviceId);
            return true;
        } catch (DeviceNotFoundException e) {
            return false;
        }
    }

    public int getDeviceCount() {
        return devices.size();
    }

    public void displayAllDevices() {
        System.out.println("=== All Devices ===");
        for (T device : devices) {
            System.out.println("  " + device);
        }
        System.out.println("Total devices: " + devices.size());
    }

    public void clearAll() {
        devices.clear();
    }
}