package master;

public class Flags {

    private boolean sensor1Flag;
    private boolean sensor2Flag;
    private boolean sensor3Flag;

    public Flags() {}

    public Flags (boolean sensor1Flag, boolean sensor2Flag, boolean sensor3Flag) {
        this.sensor1Flag = sensor1Flag;
        this.sensor2Flag = sensor2Flag;
        this.sensor3Flag = sensor3Flag;
    }

    public boolean isSensor1Flag() {
        return sensor1Flag;
    }

    public void setSensor1Flag(boolean sensor1Flag) {
        this.sensor1Flag = sensor1Flag;
    }

    public boolean isSensor2Flag() {
        return sensor2Flag;
    }

    public void setSensor2Flag(boolean sensor2Flag) {
        this.sensor2Flag = sensor2Flag;
    }

    public boolean isSensor3Flag() {
        return sensor3Flag;
    }

    public void setSensor3Flag(boolean sensor3Flag) {
        this.sensor3Flag = sensor3Flag;
    }
}
