package rest;

import database.MongoDbClient;
import database.UpdateObject;

import master.Flags;

import java.util.ArrayList;
import javax.inject.Inject;
import javax.swing.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The /history endpoints allow the client to query for different kinds of data.
 */
@Path("/history")
public class History {

    @Inject
    private MongoDbClient dbClient;

    /**
     * The /history/view{id} endpoint queries the database for the temperature history
     * of rooms with the given id.
     * @param id - ID of the room
     * @return list of update objects. Empty if no history was found for the given room.
     */
    @Path("/view{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(@PathParam("id") int id) {

        ArrayList<UpdateObject> history = dbClient.queryHistory(id);

	    return Response.ok().entity(history).build();
    }

    /**
     * This is a private class that wraps around an integer ID.
     * This is used for JAX RS to automatically convert into
     * JSON and send to the client.
     */
    private class Room {
        private int id;

        public Room() {}

        public Room(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    /**
     * The /history/rooms endpoint provides a list of all rooms that
     * are stored on the database.
     * @return list of rooms.
     */
    @Path("/rooms")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRooms() {
        ArrayList<Integer> roomsIds = dbClient.queryRooms();
        ArrayList<Room> rooms = createRooms(roomsIds);

        return Response.ok().entity(rooms).build();
    }

    private ArrayList<Room> createRooms(ArrayList<Integer> roomIds) {
        ArrayList<Room> rooms = new ArrayList<>();
        for (int i : roomIds) {
            rooms.add(new Room(i));
        }

        return rooms;
    }

    /**
     * The /history/current endpoint returns the most recently added update
     * entry from the database.
     * @return the most recent update object.
     */
    @Path("/current")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentTemps() {
        UpdateObject latest = dbClient.fetchLatestUpdate();

        return Response.ok().entity(latest).build();
    }

}
