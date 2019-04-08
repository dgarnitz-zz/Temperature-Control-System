package rest;

import database.MongoDbClient;
import database.UpdateObject;

import master.Flags;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/history")
public class History {

    @Inject
    private MongoDbClient dbClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory() {

        UpdateObject update = new UpdateObject(
	    22.3f, 16.7f, 21.0f, "now", 1, new Flags(true, true, true)
        );

	//TODO send updates if needed (change in temperature ranges)
	return Response.ok().entity(update).build();

    }

}
