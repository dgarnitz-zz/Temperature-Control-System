package master;

public class Heater {

    private boolean heaterOn = false;

    Heater() {

    }

    public void setHeater(boolean state) {
        heaterOn = state;
    }

    public boolean getHeaterState() {
        return heaterOn;
    }
}
