package server;

import java.io.IOException;
import java.net.URI;

import master.Flags;
import database.UpdateObject;
import database.MongoDbClient;
import com.mongodb.MongoException;
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

//        Flags flags = new Flags(true, true, true);
//        UpdateObject test = new UpdateObject(21.5f, 19.5f, 20.1f, "yyyy-mm-dd", 1, flags);
//        try{
//            server.dbClient.save(test);
//        } catch (MongoException ex) {
//            System.out.println(ex.getMessage());
//        }

        server.dbClient.queryAll();

         System.out.println("server.Server Started. Press Ctrl-D to exit...");
         while (System.in.read() != -1) {}
         server.shutdown();
    }
}
