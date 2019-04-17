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

@Path("/history")
public class History {

    @Inject
    private MongoDbClient dbClient;

    @Path("/view{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(@PathParam("id") int id) {
        //TODO db.fetch history based on params

        UpdateObject update = new UpdateObject(
	    22.3f, 16.7f, 21.0f, "now", 1, new Flags(true, true, true)
        );

        ArrayList<UpdateObject> history = new ArrayList<>();

        history.add(new UpdateObject(1.0f, 1.0f, 1.0f, "now", 1, new Flags(true,true,true)));
        history.add(new UpdateObject(3.0f, 3.0f, 3.0f, "now", 1, new Flags(true,true,true)));
        history.add(new UpdateObject(4.0f, 6.0f, 7.0f, "now", 1, new Flags(true,true,true)));

	    //TODO send updates if needed (change in temperature ranges)
	    return Response.ok().entity(history).build();

    }

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

    @Path("/rooms")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRooms() {

        //TODO db.fetch list of rooms
        dbClient.queryRoom(1);

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1));
        rooms.add(new Room(3));

        return Response.ok().entity(rooms).build();
    }

    @Path("/current")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentTemps() {
        UpdateObject latest = dbClient.fetchLatestUpdate();

        return Response.ok().entity(latest).build();
    }

}
