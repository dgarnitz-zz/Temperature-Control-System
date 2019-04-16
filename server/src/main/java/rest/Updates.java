package rest;

import database.MongoDbClient;
import database.UpdateObject;
import master.Flags;

import java.util.HashMap;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/update")
public class Updates {

    @Inject
    private MongoDbClient dbClient;

    private HashMap<Integer, UpdateObject> tempRanges = new HashMap<>();

    @POST
    @Path("/master")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTemperatures(UpdateObject updateObject) {
        System.out.println("UpdateObject");

        try {
            dbClient.save(updateObject);
            Flags flags = new Flags(false, false, false);
//            updateObject.setFlags(flags);
//            updateObject.setLower(30);
//            updateObject.setUpper(35);


            //TODO send updates if needed (change in temperature ranges)
            int id = updateObject.getLab();
            if (tempRanges.containsKey(id)) {
                return Response.ok().entity(tempRanges.get(id)).build();
            } else {
                return Response.ok().entity(updateObject).build();
            }

        } catch (Exception e) { //TODO particular exception
            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Database error").build();
        }
    }

    @POST
    @Path("/changetemp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeTemp(UpdateObject update) {
        int id = update.getLab();
        System.out.println("Got change temp " + id);
        tempRanges.put(id, update);

        return Response.ok().entity(update).build();

    }

}
