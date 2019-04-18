package master;

/**
 * Class to simulate the behaviour of a temperature sensor
 */
public class TempSensor {

    private float currentTemp;
    private int state = 1; // state 1 is sensor working normally, 2 is extreme change, 3 is broken
    private static final float workingTemp = 1.0f;
    private static final float extremeTemp = 20.0f;
    private static final float chanceOfBreaking = 1.0f; //1%
    private static final float chanceOfMalfunction = 1.0f; //1%
    private static final float heatTemp = 3;

    /**
     * constructor where initial temperature is passed
     * @param startTemp
     */
    public TempSensor(float startTemp) {
        currentTemp = startTemp;
    }

    /**
     * returns current temp of the sensor
     * @return currentTemp
     */
    public float getCurrentTemp() {
        return currentTemp;
    }

    /**
     * Sets the state of the sensor to working and the temperature to the last temperature of the sensor cluster
     * @param correctTemp the last temperature of the cluster cache
     */
    public void fixSensor(float correctTemp) {
        state = 1;
        currentTemp = correctTemp;
    }

    /**
     * if the sensor isn't broken then temperature is heated by set amount
     */
    public void heatTemp() {
        if (state != 3) { // if sensor isn't broken
            currentTemp += heatTemp;
        }
    }

    /**
     * state has a random chance of changing when called, whichever the state of the temperature afterwards is then
     * processed
     */
    public void tempChange() {
        randomStateChange();
        System.out.println("state: " + state);
        switch (state) {
            case 1:
                workingChange();
                break;

            case 2:
                extremeChange();
                break;

            case 3:
                currentTemp = 0;
                break;
        }
    }

    /**
     * changes state of sensor dependant on probabilities defined
     */
    private void randomStateChange() {
        float change = (float) (Math.random() * 100);
        if (change < chanceOfMalfunction) {
            state = 2;
        }
        else if (change < (chanceOfMalfunction + chanceOfBreaking)) {
            state = 3;
        }
    }

    /**
     * returns true or false given a threshold percentage that represents inc or dec
     * @param percentage the threshold
     * @return inc/dec
     */
    private boolean incOrDec(float percentage) {
        double incOrDec = Math.random();
        if (incOrDec > percentage) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 85% chance of decreasing, 15% chance of increasing by set working temperature
     */
    private void workingChange() {
        float percentage = 0.85f; //due to UK usually being colder outside than inside probability aims to reflect it (85% of the time will decrease)
        boolean increase = incOrDec(percentage);
        if (increase) {
            currentTemp += workingTemp;
        }
        else {
            currentTemp -= workingTemp;
        }
    }

    /**
     * 50% chance to increase or decrease by set extreme temperature
     */
    private void extremeChange() {
        float percentage = 0.5f; // random extreme output representing sensor malfunction probability 50%
        boolean increase = incOrDec(percentage);
        if (increase) {
            currentTemp += extremeTemp;
        }
        else {
            currentTemp -= extremeTemp;
        }
    }
}
