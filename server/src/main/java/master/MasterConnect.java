package master;

import database.UpdateObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * Class to handle the master-server connection
 */
public class MasterConnect {

    public static final String SERVER_ADDRESS = "http://localhost:8080";

    private Client client;
    private Master master;
    private UpdateObject sentObject;

    /**
     * constructor that gets passed the master it connects to the server
     * @param master
     */
    public MasterConnect(Master master) {
        client = ClientBuilder.newBuilder().build();
        this.master = master;
    }

    /**
     * returns update object of the current state of the master
     * @param endpoint the endpoint of the server that is to be connected to
     * @return the formatted object
     */
    public Response getResponse(String endpoint) {
        sentObject = createUpdate();

        return client.target(SERVER_ADDRESS + endpoint)
                .request()
                .post(Entity.json(sentObject));
    }

    /**
     * creates update of the current sate of the master
     * @return update object
     */
    private UpdateObject createUpdate() {
        return new UpdateObject(master.getUpperRange(), master.getLowerRange(), master.getCurrentTemp(), master.getDateTime(), master.getLab(), master.createFlags());
    }

    /**
     * sends and receives an update and compares them to see if any changes to the master need to be made, times out
     * after 200ms
     */
    public void processResponse() {
        Response response = getResponse("/update/master");
        System.out.println(response);
        try {
            if (response.getStatus() == 200) {
                UpdateObject receivedObject = response.readEntity(UpdateObject.class);
                compareResponse(receivedObject);
            }
        } finally {
            response.close();
        }
    }

    /**
     * Compares sent with response update, changes the range or flags if a difference is found
     * @param receivedObject
     */
    private void compareResponse(UpdateObject receivedObject) {
        if (receivedObject.getLower() != sentObject.getLower()) {
            master.setLowerRange(receivedObject.getLower());
        }
        if (receivedObject.getUpper() != sentObject.getUpper()) {
            master.setUpperRange(receivedObject.getUpper());
        }
        if (!receivedObject.getFlags().getSensor1Flag() && sentObject.getFlags().getSensor1Flag()) {
            master.fixSensor(1);
        }
        if (!receivedObject.getFlags().getSensor2Flag() && sentObject.getFlags().getSensor2Flag()) {
            master.fixSensor(2);
        }
        if (!receivedObject.getFlags().getSensor3Flag() && sentObject.getFlags().getSensor3Flag()) {
            master.fixSensor(3);
        }
    }
}
