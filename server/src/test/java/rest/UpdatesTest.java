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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UpdatesTest extends JerseyTest {

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
    public void testSameUpdate() {
        UpdateObject update = new UpdateObject(1.0f, 2.0f, 3.0f, "now", 1, new Flags());

        final Response response = target("/update/master")
                .request()
                .post(Entity.entity(update, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(update, response.readEntity(UpdateObject.class));
    }

    @Test
    public void testChangeTemp() {
        int masterId = 1;
        float upper = 5.0f;
        float lower = 3.0f;
        UpdateObject newTemps = new UpdateObject(upper, lower, 0.0f, "now", masterId, new Flags());

        Response response = target("/update/changetemp")
                .request()
                .post(Entity.entity(newTemps, MediaType.APPLICATION_JSON));

        assertEquals(newTemps, response.readEntity(UpdateObject.class));


        UpdateObject original = new UpdateObject(1.0f, 2.0f, 3.0f, "now", masterId, new Flags());

        response = target("/update/master")
                .request()
                .post(Entity.entity(original, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        UpdateObject responseUpdate = response.readEntity(UpdateObject.class);
        assertNotEquals(original, responseUpdate);
        assertEquals(upper, responseUpdate.getUpper(), 0.001);
        assertEquals(lower, responseUpdate.getLower(), 0.001);
    }
}


