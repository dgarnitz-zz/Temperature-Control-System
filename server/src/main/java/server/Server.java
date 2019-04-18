package server;

import java.io.IOException;
import java.net.URI;

import database.MongoDbClient;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main server that clients and masters can connect to.
 */
public class Server {

    public MongoDbClient dbClient;
    public HttpServer endpoints;

    public TempController tempController;

    public final String BASE_URI = "http://localhost:8080/";

    public Server() {
        this.dbClient = new MongoDbClient();
        this.tempController = new TempController();
        this.endpoints = this.start();
    }

    /**
     * Configure the Grizzly server
     * This function is required by the library.
     */
    public ResourceConfig getConfig() {
        return new ResourceConfig()
                .packages("rest")

                // Bind a database client and temperature controller
                // to the rest endpoints
                .register(new AbstractBinder() {
                    protected void configure() {
                        bind(dbClient).to(MongoDbClient.class);
                        bind(tempController).to(TempController.class);
                    }
                });
    }

    /**
     * Start the Grizzly server.
     */
    public HttpServer start() {
        final ResourceConfig rc = getConfig();

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public void shutdown() {
        endpoints.shutdown();
    }

    public static void main(String[] args) throws IOException {
        final Server server = new Server();
        System.out.println("Server Started. Press Ctrl-D to exit...");
        while (System.in.read() != -1) {}
        server.shutdown();
    }
}
