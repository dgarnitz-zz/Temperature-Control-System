package master;

import database.UpdateObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class MasterConnect {

    public static final String SERVER_ADDRESS = "http://localhost:8080";

    Client client;

    public MasterConnect() {
        client = ClientBuilder.newBuilder().build();
    }

    public Response getResponse(String endpoint) {
        UpdateObject updateObject = new UpdateObject(0.0f, 0.0f, 1.0f, "now", 1,
                new Flags(true, false, true));

        return client.target(SERVER_ADDRESS + endpoint)
                .request()
                .post(Entity.json(updateObject));
    }

    public static void main(String[] args) {

        MasterConnect mc = new MasterConnect();
        Response response = mc.getResponse("/update");

        System.out.println(response);
        try {
            if (response.getStatus() == 200) {
                UpdateObject updateObject = response.readEntity(UpdateObject.class);

                System.out.println(updateObject.getDateTime());
            }
        } finally {
            response.close();
        }

//        Master master = new Master(1);
    }
}
