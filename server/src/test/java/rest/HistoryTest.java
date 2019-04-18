package rest;

import database.MongoDbClient;
import database.UpdateObject;
import master.Flags;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import server.TempController;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HistoryTest extends JerseyTest {

    @Inject
    private MongoDbClient dbClient = new MongoDbClient();

    @Inject
    private TempController tempController = new TempController();

    @Override
    protected Application configure() {
        return new ResourceConfig().packages("rest")
                .register(new AbstractBinder() {
                    protected void configure() {
                        bind(dbClient).to(MongoDbClient.class);
                        bind(tempController).to(TempController.class);
                    }
                });
    }


    @Test
    public void testRoomHistory() {
        int masterId = new Random().nextInt(10000) + 10000;
        int numUpdates = 10;
        UpdateObject updateRoom = new UpdateObject(25.0f, 20.0f, 21.0f, "now", masterId, new Flags());
        for (int i = 0; i < numUpdates; i++) {
            dbClient.save(updateRoom);
        }

        final Response res = target("/history/view" + masterId)
                .request()
                .get();

        ArrayList<UpdateObject> updates = res.readEntity(new GenericType<ArrayList<UpdateObject>>() {});
        assertTrue(updates.size() >= numUpdates);

        for (UpdateObject update : updates) {
            assertEquals(masterId, update.getLab());
        }
    }


    @Test
    public void testGetCurrent() {
        UpdateObject update = new UpdateObject(10f, 5f, 4f, "now", 1, new Flags());
        dbClient.save(update);

        final Response res = target("/history/current")
                .request()
                .get();

        UpdateObject currentUpdate = res.readEntity(UpdateObject.class);
        assertEquals(update, currentUpdate);
    }

}
