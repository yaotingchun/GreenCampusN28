package core;
import control.Admin;

public interface Controllable {
    void turnOn(Admin admin);
    void turnOff(Admin admin);
    boolean isOn();
    String getStatus();
}
