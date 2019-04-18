package rest;

import database.MongoDbClient;
import database.UpdateObject;
import master.Flags;
import server.TempController;

import java.util.HashMap;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This is the update endpoint that provides endpoints for the master to send its
 * periodic updates, and for the client to provide and updates to change the
 * temperature ranges.
 */
@Path("/update")
public class Updates {

    @Inject
    private MongoDbClient dbClient;

    @Inject
    private TempController tempController;

    /**
     * 
     * @param updateObject
     * @return
     */
    @POST
    @Path("/master")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTemperatures(UpdateObject updateObject) {
        System.out.println("Got update from master id: " + updateObject.getLab());

        try {
            dbClient.save(updateObject);

            int id = updateObject.getLab();
            Optional<UpdateObject> newRanges = tempController.getTemperatureChange(id);

            if (newRanges.isPresent()) {
                UpdateObject newUpdate = newRanges.get();

                updateObject.setUpper(newUpdate.getUpper());
                updateObject.setLower(newUpdate.getLower());
            }

            return Response.ok().entity(updateObject).build();

        } catch (Exception e) {
            e.printStackTrace();

            return Response.status(
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Database error").build();
        }
    }

    /**
     *
     * @param update
     * @return
     */
    @POST
    @Path("/changetemp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeTemp(UpdateObject update) {
        int id = update.getLab();
        System.out.println("Got change temp " + id);

        tempController.changeTemperature(id, update);

        return Response.ok().entity(update).build();
    }
}
