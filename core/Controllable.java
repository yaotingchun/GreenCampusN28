package core;
import Control.Admin;

public interface Controllable {
    void turnOn(Admin admin);
    void turnOff(Admin admin);
    boolean isOn();
    String getStatus();
}
