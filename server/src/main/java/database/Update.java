package database;

public class Update {

    private int masterId;
    private int temperature;
    private boolean flag;
    private boolean heater;

    public Update(int masterId, int temperature, boolean flag, boolean heater) {
        this.masterId = masterId;
        this.temperature = temperature;
        this.flag = flag;
        this.heater = heater;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isHeater() {
        return heater;
    }

    public void setHeater(boolean heater) {
        this.heater = heater;
    }
}
