package it.trenical.server;

import it.trenical.server.connection.GrpcServer;

public class Server {
    public static void main(String[] args) throws Exception {

        GrpcServer server = GrpcServer.getInstance();
        server.start();
        server.blockUntilShutdown();
    }
}
