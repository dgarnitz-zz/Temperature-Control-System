package server;

import java.io.IOException;
import java.net.URI;

import database.MongoDbClient;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {

    public MongoDbClient dbClient;
    public HttpServer endpoints;

    public final String BASE_URI = "http://localhost:8080/";

    public Server() {
        this.dbClient = new MongoDbClient();
        this.endpoints = this.start();
    }

    public ResourceConfig getConfig() {
        return new ResourceConfig()
                .packages("rest")
                .register(new AbstractBinder() {
                    protected void configure() {
                        bind(dbClient).to(MongoDbClient.class);
                    }
                });
    }

    public HttpServer start() {
        final ResourceConfig rc = getConfig();

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public void shutdown() {
        endpoints.shutdown();
    }

    public static void main(String[] args) throws IOException {
        final Server server = new Server();
        System.out.println("server.Server Started. Press Ctrl-D to exit...");
        while (System.in.read() != -1) {}
        server.shutdown();
    }
}

