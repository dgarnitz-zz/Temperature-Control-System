package master;


public class UpdateObject {
    private float upper;
    private float lower;
    private float currentTemp;
    private String dateTime;
    private int lab;
    private Flags flags;

    UpdateObject(float upper, float lower, float currentTemp, String dateTime, int lab, Flags flags) {
        this.upper = upper;
        this.lower = lower;
        this.currentTemp = currentTemp;
        this.dateTime = dateTime;
        this.lab = lab;
        this.flags = flags;
    }

}
