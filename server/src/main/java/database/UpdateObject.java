package database;


import master.Flags;

public class UpdateObject {
    private float upper;
    private float lower;
    private float currentTemp;
    private String dateTime;
    private int lab;
    private Flags flags;

    public UpdateObject() {}

    public UpdateObject(float upper, float lower, float currentTemp, String dateTime, int lab, Flags flags) {
        this.upper = upper;
        this.lower = lower;
        this.currentTemp = currentTemp;
        this.dateTime = dateTime;
        this.lab = lab;
        this.flags = flags;
    }

    public float getUpper() {
        return upper;
    }

    public void setUpper(float upper) {
        this.upper = upper;
    }

    public float getLower() {
        return lower;
    }

    public void setLower(float lower) {
        this.lower = lower;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getLab() {
        return lab;
    }

    public void setLab(int lab) {
        this.lab = lab;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }
}
