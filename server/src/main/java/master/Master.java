package master;

import database.UpdateObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the Master package, contains the core logic and functionality.
 */
public class Master {

    private int lab;
    private static final int highTemp = 20; //this multiplies 0-1 by val
    private static final int lowTemp = 10; //this adds val to result of previous multiplication (i.e. 0-1 * 20 = 0-20 ... 0-20 + 10 = 10-30)
    private static final float tempThreshold = 10; //how different sensor clusters can be from each other
    private static final int sensorCacheStorage = 100;
    private static final float initialLowRange = 20;
    private static final float initialUpperRange = 25;
    private static final int timeStepSeconds = 10;

    private TempSensor tempSensor1;
    private TempSensor tempSensor2;
    private TempSensor tempSensor3;
    private boolean sensor1Flag = false;
    private boolean sensor2Flag = false;
    private boolean sensor3Flag = false;
    private ArrayList<Float> sensor1Cache;
    private ArrayList<Float> sensor2Cache;
    private ArrayList<Float> sensor3Cache;
    private ArrayList<Float> clusterCache;

    private Heater heater;
    private float lowRange;
    private float highRange;

    private DateFormat df;
    private MasterConnect masterConnect;

    /**
     * Constructor that initialises all temp sensors, heaters, caches, master connect.
     * @param lab the lab number
     */
    public Master(int lab) {
        this.lab = lab;
        float startTemp = createStartTemp();
        tempSensor1 = new TempSensor(startTemp);
        tempSensor2 = new TempSensor(startTemp);
        tempSensor3 = new TempSensor(startTemp);
        sensor1Cache = new ArrayList<>();
        sensor2Cache = new ArrayList<>();
        sensor3Cache = new ArrayList<>();
        clusterCache = new ArrayList<>();
        heater = new Heater();
        setLowerRange(initialLowRange);
        setUpperRange(initialUpperRange);
        df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        masterConnect = new MasterConnect(this);
    }

    public boolean isSensor1Flag() {
        return sensor1Flag;
    }

    public void setSensor1Flag(boolean sensor1Flag) {
        this.sensor1Flag = sensor1Flag;
    }

    public MasterConnect getMasterConnect() {
        return masterConnect;
    }

    public TempSensor getTempSensor1() {
        return tempSensor1;
    }

    public Heater getHeater() {
        return heater;
    }

    public void setHeater(Heater heater) {
        this.heater = heater;
    }

    /**
     * sets lower range for the temperature of the room
     * @param lowRange
     */
    public void setLowerRange(float lowRange) {
        this.lowRange = lowRange;
    }

    /**
     * sets the upper range for the temperature of the room
     * @param highRange
     */
    public void setUpperRange(float highRange) {
        this.highRange = highRange;
    }

    /**
     * returns lower range
     * @return lowRange
     */
    public float getLowerRange() {
        return lowRange;
    }

    /**
     * returns the upper range
     * @return highRange
     */
    public float getUpperRange() {
        return highRange;
    }

    /**
     * returns the latest temperature added to the cluster cache
     * @return current temperature
     */
    public float getCurrentTemp() {
        return clusterCache.get(clusterCache.size() - 1);
    }

    /**
     * returns the lab number of the master
     * @return lab
     */
    public int getLab() {
        return lab;
    }

    /**
     * Fixes the sensor that's passed
     * @param sensor number of which sensor to fix
     */
    public void fixSensor(int sensor) {
        if (sensor >= 1 && sensor <= 3) {
            switch(sensor) {
                case 1:
                    if (sensor1Flag) {
                        tempSensor1.fixSensor(getCurrentTemp());
                        sensor1Flag = false;
                        break;
                    }
                    else {
                        System.out.println("Sensor 1 isn't flagged and is working correctly");
                        break;
                    }

                case 2:
                    if (sensor2Flag) {
                        tempSensor2.fixSensor(getCurrentTemp());
                        sensor2Flag = false;
                        break;
                    }
                    else {
                        System.out.println("Sensor 2 isn't flagged and is working correctly");
                        break;
                    }

                case 3:
                    if (sensor3Flag) {
                        tempSensor3.fixSensor(getCurrentTemp());
                        sensor3Flag = false;
                        break;
                    }
                    else {
                        System.out.println("Sensor 3 isn't flagged and is working correctly");
                        break;
                    }
            }
        }
        else {
            System.out.println("There are only three sensors in a cluster");
        }
    }

    /**
     * creates a random starting temperature between 10-30 for all sensors. (public for test)
     * @return temperature between 10-30 in one dp
     */
    public float createStartTemp() {
        double randomDouble = Math.random(); //0-1
        double tempDouble = (randomDouble * highTemp) + lowTemp; //10-30
        return roundOneDp(tempDouble); //converts to 1dp
    }

    /**
     * rounds a double to a one dp float (public for test)
     * @param temp the temperature to be rounded
     * @return rounded temperature
     */
    public float roundOneDp(double temp) {
        return (float) (Math.round(temp * 10) / 10.0); //converts to 1dp
    }

    /**
     * Called at every time step to check the functionality of the sensors (heartbeat), add the average of the
     * functioning sensors to the cluster cache, checks the temperature room of the room and heats accordingly and
     * applies temp change to each sensor.
     */
    public void sensorUpdate() {
        checkTemps();
        addToClusterCache();
        checkRange();
        System.out.println("sensor1: " + sensor1Cache.get(sensor1Cache.size() -1));
        System.out.println("sensor2: " + sensor2Cache.get(sensor2Cache.size() -1));
        System.out.println("sensor3: " + sensor3Cache.get(sensor3Cache.size() -1));
        System.out.println("range: " + lowRange + " - " + highRange);
        heat();
        tempChange();
        System.out.println("temp after processing: " + getCurrentTemp());
    }

    /**
     * If the heater is on, heats each sensor
     */
    public void heat() {
        if(heater.getHeaterState()) {
            tempSensor1.heatTemp();
            tempSensor2.heatTemp();
            tempSensor3.heatTemp();
        }
    }

    /**
     * applies random temperature change to each sensor
     */
    private void tempChange() {
        tempSensor1.tempChange();
        tempSensor2.tempChange();
        tempSensor3.tempChange();
    }

    /**
     * applies the hysteresis loop explained in the report to safely control the heater
     */
    private void checkRange() {
        if (getCurrentTemp() < lowRange) {
            heater.setHeater(true);
        }
        else if (getCurrentTemp() > highRange) {
            heater.setHeater(false);
        }
    }

    /**
     * Uses a heartbeat method to ensure the functionality of the sensors, if a sensor is detected as malfunctioning
     * it is flagged
     */
    public void checkTemps() {
        float temp1 = tempSensor1.getCurrentTemp();
        float temp2 = tempSensor2.getCurrentTemp();
        float temp3 = tempSensor3.getCurrentTemp();
        if (Math.abs(temp1 - temp2) < tempThreshold && Math.abs(temp1 - temp3) < tempThreshold && Math.abs(temp2 - temp3) < tempThreshold && !sensor1Flag && !sensor2Flag && !sensor3Flag) {

            addToSensorCache(temp1, 1);

            addToSensorCache(temp2, 2);

            addToSensorCache(temp3, 3);
        }
        else if ((Math.abs(temp1 - temp2) < tempThreshold) && !sensor1Flag && !sensor2Flag) {

            addToSensorCache(temp1, 1);

            addToSensorCache(temp2, 2);

            System.out.println("FLAG: Sensor 3 vastly different, investigate sensor 3");
            sensor3Flag = true;
            addToSensorCache(temp3, 3);

        }
        else if ((Math.abs(temp1 - temp3) < tempThreshold) && !sensor1Flag && !sensor3Flag) {

            addToSensorCache(temp1, 1);

            System.out.println("FLAG: Sensor 2 vastly different, investigate sensor 2");
            sensor2Flag = true;
            addToSensorCache(temp2, 2);

            addToSensorCache(temp3, 3);
        }
        else if ((Math.abs(temp2 - temp3) < tempThreshold) && !sensor2Flag && !sensor3Flag) {
            System.out.println("FLAG: Sensor 1 vastly different, investigate sensor 1");
            sensor1Flag = true;
            addToSensorCache(temp1, 1);

            addToSensorCache(temp2, 2);

            addToSensorCache(temp3, 3);
        }
        else { //if no sensors are within threshold then checks which is within threshold of previous temp in cache and uses that
            if ((Math.abs(temp1 - clusterCache.get(clusterCache.size() - 1)) < tempThreshold) && !sensor1Flag) {
                addToSensorCache(temp1, 1);
            }
            else {
                System.out.println("FLAG: current temp1 vastly different from previous, investigate error");
                sensor1Flag = true;
                addToSensorCache(temp1, 1);
            }
            if ((Math.abs(temp2 - clusterCache.get(clusterCache.size() - 1)) < tempThreshold) && !sensor2Flag) {
                addToSensorCache(temp2, 2);
            }
            else {
                System.out.println("FLAG: current temp2 vastly different from previous, investigate error");
                sensor2Flag = true;
                addToSensorCache(temp2, 2);
            }
            if ((Math.abs(temp3 - clusterCache.get(clusterCache.size() - 1)) < tempThreshold) && !sensor3Flag) {
                addToSensorCache(temp3, 3);
            }
            else {
                System.out.println("FLAG: current temp3 vastly different from previous, investigate error");
                sensor3Flag = true;
                addToSensorCache(temp3, 3);
            }
        }
    }

    /**
     * Adds the average of the functioning sensors to the cluster cache
     */
    public void addToClusterCache() {
        float temp;
        int index = sensor1Cache.size() - 1;
        if (!sensor1Flag && !sensor2Flag && !sensor3Flag) {
            temp = (sensor1Cache.get(index) + sensor2Cache.get(index) + sensor3Cache.get(index)) / 3;
            clusterCache.add(roundOneDp(temp));
        }
        else if (!sensor1Flag && !sensor2Flag) {
            temp = (sensor1Cache.get(index) + sensor2Cache.get(index)) / 2;
            clusterCache.add(roundOneDp(temp));
        }
        else if (!sensor1Flag && !sensor3Flag) {
            temp = (sensor1Cache.get(index) + sensor3Cache.get(index)) / 2;
            clusterCache.add(roundOneDp(temp));
        }
        else if (!sensor2Flag && !sensor3Flag) {
            temp = (sensor2Cache.get(index) + sensor3Cache.get(index)) / 2;
            clusterCache.add(roundOneDp(temp));
        }
        else if (!sensor1Flag) {
            temp = sensor1Cache.get(index);
            clusterCache.add(roundOneDp(temp));
        }
        else if (!sensor2Flag) {
            temp = sensor2Cache.get(index);
            clusterCache.add(roundOneDp(temp));
        }
        else if (!sensor3Flag) {
            temp = sensor3Cache.get(index);
            clusterCache.add(roundOneDp(temp));
        }
    }


    /**
     * Adds the temperature of the sensor to its cache
     * @param temp the temperature of the sensor
     * @param sensor the associated sensor
     */
    private void addToSensorCache(float temp, int sensor) {
        switch (sensor) {
            case 1:
                if ((sensor1Cache.size() - 1) <= sensorCacheStorage) {
                    sensor1Cache.add(temp);
                    break;
                }
                else {
                    sensor1Cache.remove(0);
                    sensor1Cache.add(temp);
                    break;
                }

            case 2:
                if ((sensor2Cache.size() - 1) <= sensorCacheStorage) {
                    sensor2Cache.add(temp);
                    break;
                }
                else {
                    sensor2Cache.remove(0);
                    sensor2Cache.add(temp);
                    break;
                }

            case 3:
                if ((sensor3Cache.size() - 1) <= sensorCacheStorage) {
                    sensor3Cache.add(temp);
                    break;
                }
                else {
                    sensor3Cache.remove(0);
                    sensor3Cache.add(temp);
                    break;
                }
        }
    }

    /**
     * Creates flags that represent the state of the cluster
     * @return flag object of the three flags
     */
    public Flags createFlags() {
        return new Flags(this.sensor1Flag, this.sensor2Flag, this.sensor3Flag);
    }

    /**
     * returns a formatted date time string
     * @return formatted date time
     */
    public String getDateTime() {
        Date dateTime = new Date();
        return df.format(dateTime);
    }

    /**
     * infinite loop where an update is sent and received at a set time step
     * @throws InterruptedException
     */
    private void runLoop() throws InterruptedException {
        while(true) {
            sensorUpdate();
            masterConnect.processResponse();
            TimeUnit.SECONDS.sleep(timeStepSeconds);
        }
    }

    /**
     * creates a master object
     * @param args
     */
    public static void main(String[] args) {
        int i = Integer.valueOf(args[0]);
        Master master = new Master(i);
        try {
            master.runLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}