package rest;

import database.MongoDbClient;
import database.UpdateObject;
import master.Flags;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class UpdatesTest extends JerseyTest {

    @Inject
    private MongoDbClient dbClient = new MongoDbClient();

    @Override
    protected Application configure() {
        return new ResourceConfig().packages("rest")
                .register(new AbstractBinder() {
                    protected void configure() {
                        bind(dbClient).to(MongoDbClient.class);
                    }
                });
    }

    @Test
    public void testSameUpdate() {
        UpdateObject update = new UpdateObject(1.0f, 2.0f, 3.0f, "now", 1, new Flags(true, true, false));

        final Response response = target("/update")
                .request()
                .post(Entity.entity(update, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(update, response.readEntity(UpdateObject.class));
    }
}


