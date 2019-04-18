import database.UpdateObject;
import junit.framework.TestCase;
import static org.junit.Assert.*;

import master.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class MasterTest {
    private Master testMaster;
    private static final int lab = 1;
    private TempSensor tempSensor1;
    private Heater heater;
    private MasterConnect masterConnect;

    @Before
    public void setup(){
        testMaster = new Master(lab);
        tempSensor1 = testMaster.getTempSensor1();
        heater = testMaster.getHeater();
        masterConnect = testMaster.getMasterConnect();
    }

    @Test
    public void getUpperTest() {
        assertEquals(25.0f, testMaster.getUpperRange(), 0.001);
    }

    @Test
    public void getLowerTest() {
        assertEquals(20.0f, testMaster.getLowerRange(), 0.001);
    }

    @Test
    public void testLabNumber() {
        assertEquals(lab, testMaster.getLab());
    }

    @Test
    public void testRounding() {
        double temp = 10.55555555;
        assertEquals(10.6f, testMaster.roundOneDp(temp),0.001);
    }

    @Test
    public void testHeating() {
        float beforeHeat = tempSensor1.getCurrentTemp();
        heater.setHeater(true);
        testMaster.heat();
        float afterHeat = tempSensor1.getCurrentTemp();
        assertEquals(beforeHeat + 3, afterHeat, 0.001);
    }

    @Test
    public void testRecievedHandling() {
        testMaster.checkTemps();
        testMaster.addToClusterCache();
        masterConnect.setSentObject(masterConnect.createUpdate());
        UpdateObject newState = masterConnect.createUpdate();
        newState.setLower(30f);
        newState.setUpper(35f);
        masterConnect.compareResponse(newState);
        assertEquals(testMaster.getLowerRange(), newState.getLower(), 0.001);
        assertEquals(testMaster.getUpperRange(), newState.getUpper(), 0.001);
    }

    @Test
    public void testFixingSensor() {
        Flags testFlags = new Flags(false, false, false);
        testMaster.setSensor1Flag(true);
        testMaster.checkTemps();
        testMaster.addToClusterCache();
        masterConnect.setSentObject(masterConnect.createUpdate());
        UpdateObject newState = masterConnect.createUpdate();
        newState.setFlags(testFlags);
        masterConnect.compareResponse(newState);
        assertFalse(testMaster.isSensor1Flag());
    }

}
