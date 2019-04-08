import junit.framework.TestCase;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import master.Master;

public class MasterTest {
    Master testMaster;

    @Before
    public void setup(){
        testMaster = new Master(1);
    }

    @Test
    public void fakeTest() {
        assertEquals(25.0f, testMaster.getHighRange(), 0);
    }

}
