package master;

/**
 * Class to store flag states to sent in the update object
 */
public class Flags {

    private boolean sensor1Flag;
    private boolean sensor2Flag;
    private boolean sensor3Flag;


    public Flags() {}

    /**
     * Constructor creating flag states of the object
     * @param sensor1Flag
     * @param sensor2Flag
     * @param sensor3Flag
     */
    public Flags (boolean sensor1Flag, boolean sensor2Flag, boolean sensor3Flag) {
        this.sensor1Flag = sensor1Flag;
        this.sensor2Flag = sensor2Flag;
        this.sensor3Flag = sensor3Flag;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Flags) {
            Flags other = (Flags) o;
            return this.sensor1Flag == other.sensor1Flag
                    && this.sensor2Flag == other.sensor2Flag
                    && this.sensor3Flag == other.sensor3Flag;

        } else {
            return false;
        }
    }


    /**
     * returns senor 1 flag state
     * @return sensor1Flag
     */
    public boolean getSensor1Flag() {
        return sensor1Flag;
    }

    /**
     * sets sensor 1 flag state
     * @param sensor1Flag
     */
    public void setSensor1Flag(boolean sensor1Flag) {
        this.sensor1Flag = sensor1Flag;
    }

    /**
     * returns sensor 2 flag state
     * @return sensor2Flag
     */
    public boolean getSensor2Flag() {
        return sensor2Flag;
    }

    /**
     * sets sensor 2 flag state
     * @param sensor2Flag
     */
    public void setSensor2Flag(boolean sensor2Flag) {
        this.sensor2Flag = sensor2Flag;
    }

    /**
     * returns sensor 3 flag state
     * @return sensor3Flag
     */
    public boolean getSensor3Flag() {
        return sensor3Flag;
    }

    /**
     * set sensor 3 flag state
     * @param sensor3Flag
     */
    public void setSensor3Flag(boolean sensor3Flag) {
        this.sensor3Flag = sensor3Flag;
    }
}
