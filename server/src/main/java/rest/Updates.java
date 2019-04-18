package rest;

import database.MongoDbClient;
import database.UpdateObject;
import server.TempController;

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
     * The /update/master endpoint is the one a master uses to send its periodic updates.
     * Updates received are stored in the database and if there are any changes
     * in temperature range, this is sent back to the master.
     * @param updateObject - Update from the master to the server.
     * @return an update object with changes is any are required. Otherwise it is the
     * same update object as the one sent from the master.
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
     * The /update/changetemp endpoint allows the client to send an update object
     * with new temperature ranges. This change in temperature is recorded locally
     * and sent to the relevant master when the next update from that master is received.
     * @param temperatureUpdate - Update object sent by the client.
     * @return the same update object sent by the client.
     */
    @POST
    @Path("/changetemp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeTemp(UpdateObject temperatureUpdate) {
        int id = temperatureUpdate.getLab();
        System.out.println("Got change temp " + id);

        tempController.changeTemperature(id, temperatureUpdate);

        return Response.ok().entity(temperatureUpdate).build();
    }
}
