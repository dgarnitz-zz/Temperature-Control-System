package server;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {
    public static final String BASE_URI = "http://localhost:8080/";

    public static ResourceConfig getConfig() {
        return new ResourceConfig().packages("rest");
    }

    public static HttpServer start() {
        final ResourceConfig rc = getConfig();

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = Server.start();
        System.out.println("server.Server Started. Press Ctrl-D to exit...");
        while (System.in.read() != -1) {}
        server.stop();
    }
}

