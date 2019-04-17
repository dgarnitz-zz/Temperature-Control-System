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

        ArrayList<UpdateObject> history = dbClient.queryHistory(id);

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

        ArrayList<UpdateObject> rooms = new ArrayList<UpdateObject>();

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
