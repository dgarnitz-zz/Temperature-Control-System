package master;

public class TempSensor {

    private float currentTemp;
    private int state = 1; // 1 is sensor working normally, 2 is extreme change, 3 is broken
    private float extremeTemp = 20.0f;
    private float chanceOfBreaking = 1.0f; //1%
    private float chanceOfMalfunction = 1.0f; //1%

    public TempSensor(float startTemp) {
        currentTemp = startTemp;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public int getSensorState() {
        return state;
    }

    public void changeState(int stateChange) { //to accommodate for maintenance fixing broken/malfunctioning sensor
        if (stateChange >= 1 && stateChange <= 3) {
            state = stateChange;
        }
        else {
            System.out.println("There are only 3 possible states:\n 1 is sensor working normally, 2 is extreme change, 3 is broken");
        }
    }

    public void fixSensor(float correctTemp) {
        state = 1;
        currentTemp = correctTemp;
    }

    public void heatTemp() {
        if (state != 3) { // if sensor isn't broken
            currentTemp += 3;
        }
        else {
            System.out.println("Sensor broken"); // needs correct error handling, but highlights when sensor is broken
        }
    }

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

    private void randomStateChange() { //changes state of sensor dependant on probabilities defined
        float change = (float) (Math.random() * 100);
        if (change < chanceOfMalfunction) {
            state = 2;
        }
        else if (change < (chanceOfMalfunction + chanceOfBreaking)) {
            state = 3;
        }
    }

    private boolean incOrDec(float percentage) { // returns true or false given a threshold that represents inc or dec
        double incOrDec = Math.random();
        if (incOrDec > percentage) {
            return true;
        }
        else {
            return false;
        }
    }

    private void workingChange() {
        float percentage = 0.85f; //due to UK usually being colder outside than inside probability aims to reflect it (85% of the time will decrease)
        boolean increase = incOrDec(percentage);
        if (increase) {
            currentTemp += 1;
        }
        else {
            currentTemp -= 1;
        }
    }

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
