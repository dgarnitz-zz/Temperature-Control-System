package master;

/**
 * Class for storing the state of the heater in a lab
 */
public class Heater {

    private boolean heaterOn ;

    /**
     * Constructor sets heater to be off by default
     */
    Heater() {
        heaterOn = false;
    }

    /**
     * sets heater state to boolean passed
     * @param state
     */
    public void setHeater(boolean state) {
        heaterOn = state;
    }

    /**
     * returns the state of the heater
     * @return heaterOn
     */
    public boolean getHeaterState() {
        return heaterOn;
    }
}
