package master;

import database.UpdateObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Master {

    private int lab;

    private TempSensor tempSensor1;
    private TempSensor tempSensor2;
    private TempSensor tempSensor3;
    private boolean sensor1Flag = false;
    private boolean sensor2Flag = false;
    private boolean sensor3Flag = false;
    private int highTemp = 20; //this multiplies 0-1 by val
    private int lowTemp = 10; //this adds val to result of previous multiplication (i.e. 0-1 * 20 = 0-20 ... 0-20 + 10 = 10-30)
    private float tempThreshold = 10; //how different sensor clusters can be from each other
    private ArrayList<Float> sensor1Cache;
    private ArrayList<Float> sensor2Cache;
    private ArrayList<Float> sensor3Cache;
    private ArrayList<Float> clusterCache;
    private int sensorCacheStorage = 100;
    private boolean temp1Check;
    private boolean temp2Check;
    private boolean temp3Check;

    private Heater heater;
    private float lowRange;
    private float highRange;
    private float hysteresisValue = 2.5f;

    private DateFormat df;

    Master(int lab) {
        this.lab = lab;
        float startTemp = createStartTemp(highTemp, lowTemp);
        tempSensor1 = new TempSensor(startTemp);
        tempSensor2 = new TempSensor(startTemp);
        tempSensor3 = new TempSensor(startTemp);
        sensor1Cache = new ArrayList<>();
        sensor2Cache = new ArrayList<>();
        sensor3Cache = new ArrayList<>();
        clusterCache = new ArrayList<>();
        heater = new Heater();
        df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        setRange(20,25);
        try {
            runLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setRange(float lowRange, float highRange) {
        this.lowRange = lowRange;
        this.highRange = highRange;
    }

    public float getLowRange() {
        return lowRange;
    }

    public float getHighRange() {
        return highRange;
    }

    public void fixSensor(int sensor) {
        if (sensor >= 1 && sensor <= 3) {
            switch(sensor) {
                case 1:
                    if (sensor1Flag) {
                        tempSensor1.fixSensor(clusterCache.get(clusterCache.size() - 1));
                        sensor1Flag = false;
                        break;
                    }
                    else {
                        System.out.println("Sensor 1 isn't flagged and is working correctly");
                        break;
                    }

                case 2:
                    if (sensor2Flag) {
                        tempSensor2.fixSensor(clusterCache.get(clusterCache.size() - 1));
                        sensor2Flag = false;
                        break;
                    }
                    else {
                        System.out.println("Sensor 2 isn't flagged and is working correctly");
                        break;
                    }

                case 3:
                    if (sensor3Flag) {
                        tempSensor3.fixSensor(clusterCache.get(clusterCache.size() - 1));
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

    private float createStartTemp(int highTemp, int lowTemp) {
        double randomDouble = Math.random(); //0-1
        double tempDouble = (randomDouble * highTemp) + lowTemp; //10-30
        return roundOneDp(tempDouble); //converts to 1dp
    }

    private float roundOneDp(double temp) {
        return (float) (Math.round(temp * 10) / 10.0); //converts to 1dp
    }

    private void sensorUpdate() {
        checkTemps();
        addToClusterCache();
        checkRange();
        System.out.println("sensor1: " + sensor1Cache.get(sensor1Cache.size() -1));
        System.out.println("sensor2: " + sensor2Cache.get(sensor2Cache.size() -1));
        System.out.println("sensor3: " + sensor3Cache.get(sensor3Cache.size() -1));
        System.out.println("range: " + lowRange + " - " + highRange);
        if(heater.getHeaterState()) {
            tempSensor1.heatTemp();
            tempSensor2.heatTemp();
            tempSensor3.heatTemp();
        }
        tempSensor1.tempChange();
        tempSensor2.tempChange();
        tempSensor3.tempChange();
        System.out.println("temp after processing: " + clusterCache.get(clusterCache.size() - 1));
    }

    private void checkRange() {
        if (clusterCache.get(clusterCache.size() - 1) < (lowRange - hysteresisValue)) {
            heater.setHeater(true);
        }
        else if (clusterCache.get(clusterCache.size() - 1) > (highRange + hysteresisValue)) {
            heater.setHeater(false);
        }
    }

    private void checkTemps() {
        float temp1 = tempSensor1.getCurrentTemp();
        float temp2 = tempSensor2.getCurrentTemp();
        float temp3 = tempSensor3.getCurrentTemp();
        temp1Check = false;
        temp2Check = false;
        temp3Check = false;
        if (Math.abs(temp1 - temp2) < tempThreshold && Math.abs(temp1 - temp3) < tempThreshold && Math.abs(temp2 - temp3) < tempThreshold && !sensor1Flag && !sensor2Flag && !sensor3Flag) {
            temp1Check = true;
            addToSensorCache(temp1, 1);

            temp2Check = true;
            addToSensorCache(temp2, 2);

            temp3Check = true;
            addToSensorCache(temp3, 3);
            System.out.println("ALL IN RANGE");
        }
        else if ((Math.abs(temp1 - temp2) < tempThreshold) && !sensor1Flag && !sensor2Flag) {

            temp1Check = true;
            addToSensorCache(temp1, 1);

            temp2Check = true;
            addToSensorCache(temp2, 2);

            System.out.println("FLAG: Sensor 3 vastly different, investigate sensor 3");
            sensor1Flag = true;
            addToSensorCache(temp3, 3);

        }
        else if ((Math.abs(temp1 - temp3) < tempThreshold) && !sensor1Flag && !sensor3Flag) {

            temp1Check = true;
            addToSensorCache(temp1, 1);

            System.out.println("FLAG: Sensor 2 vastly different, investigate sensor 2");
            sensor2Flag = true;
            addToSensorCache(temp2, 2);

            temp3Check = true;
            addToSensorCache(temp3, 3);
        }
        else if ((Math.abs(temp2 - temp3) < tempThreshold) && !sensor2Flag && !sensor3Flag) {
            System.out.println("FLAG: Sensor 1 vastly different, investigate sensor 1");
            sensor1Flag = true;
            addToSensorCache(temp1, 1);

            temp2Check = true;
            addToSensorCache(temp2, 2);

            temp3Check = true;
            addToSensorCache(temp3, 3);
        }
        else { //if no sensors are within threshold then checks which is within threshold of previous temp in cache and uses that
            if ((Math.abs(temp1 - clusterCache.get(clusterCache.size() - 1)) < tempThreshold) && !sensor1Flag) {
                temp1Check = true;
                addToSensorCache(temp1, 1);
            }
            else {
                System.out.println("FLAG: current temp1 vastly different from previous, investigate error");
                sensor1Flag = true;
                addToSensorCache(temp1, 1);
            }
            if ((Math.abs(temp2 - clusterCache.get(clusterCache.size() - 1)) < tempThreshold) && !sensor2Flag) {
                temp2Check = true;
                addToSensorCache(temp2, 2);
            }
            else {
                System.out.println("FLAG: current temp2 vastly different from previous, investigate error");
                sensor2Flag = true;
                addToSensorCache(temp2, 2);
            }
            if ((Math.abs(temp3 - clusterCache.get(clusterCache.size() - 1)) < tempThreshold) && !sensor3Flag) {
                temp3Check = true;
                addToSensorCache(temp3, 3);
            }
            else {
                System.out.println("FLAG: current temp3 vastly different from previous, investigate error");
                sensor3Flag = true;
                addToSensorCache(temp3, 3);
            }
        }
    }
    private void addToClusterCache() {
        float temp;
        int index = sensor1Cache.size() - 1;
        if (temp1Check && temp2Check && temp3Check) {
            temp = (sensor1Cache.get(index) + sensor2Cache.get(index) + sensor3Cache.get(index)) / 3;
            clusterCache.add(roundOneDp(temp));
        }
        else if (temp1Check && temp2Check) {
            temp = (sensor1Cache.get(index) + sensor2Cache.get(index)) / 2;
            clusterCache.add(roundOneDp(temp));
        }
        else if (temp1Check && temp3Check) {
            temp = (sensor1Cache.get(index) + sensor3Cache.get(index)) / 2;
            clusterCache.add(roundOneDp(temp));
        }
        else if (temp2Check && temp3Check) {
            temp = (sensor2Cache.get(index) + sensor3Cache.get(index)) / 2;
            clusterCache.add(roundOneDp(temp));
        }
        else if (temp1Check) {
            temp = sensor1Cache.get(index);
            clusterCache.add(roundOneDp(temp));
        }
        else if (temp2Check) {
            temp = sensor2Cache.get(index);
            clusterCache.add(roundOneDp(temp));
        }
        else if (temp3Check) {
            temp = sensor3Cache.get(index);
            clusterCache.add(roundOneDp(temp));
        }
    }

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

    private void createUpdate() {
        float upper = getHighRange();
        float lower = getLowRange();
        float currentTemp = clusterCache.get(clusterCache.size() - 1);
        String dateTime = dateTime();
        int lab = this.lab;
        Flags flags = createFlags();
        UpdateObject update = new UpdateObject(upper, lower, currentTemp, dateTime, lab, flags);
    }

    private Flags createFlags() {
        return new Flags(this.sensor1Flag, this.sensor2Flag, this.sensor3Flag);
    }

    private String dateTime() {
        Date dateTime = new Date();
        return df.format(dateTime);
    }

    private void runLoop() throws InterruptedException {
        Instant start = Instant.now();

        while(true) {
            sensorUpdate();
            createUpdate();
            TimeUnit.SECONDS.sleep(10);
        }
    }
}