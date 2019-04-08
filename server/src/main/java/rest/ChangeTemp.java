package rest;

import database.MongoDbClient;
import database.UpdateObject;

import master.Flags;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/changetemp")
public class ChangeTemp {

    @Inject
    private MongoDbClient dbClient;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response changeTemp(UpdateObject update) {

        System.out.println(update.getUpper());

	return Response.ok().build();

    }

}
