package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This is a healthcheck endpoint to make sure the server is
 * running.
 */
@Path("/ping")
public class Ping {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "ping";
    }
}
