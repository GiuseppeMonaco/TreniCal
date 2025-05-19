package it.trenical.server;

import it.trenical.server.connection.GrpcServer;
import it.trenical.server.db.SQLite.SQLiteConnection;

public class Server {
    public static void main(String[] args) throws Exception {

        // Database initialization
        SQLiteConnection.getInstance();

        GrpcServer server = GrpcServer.getInstance();
        server.start();
        server.blockUntilShutdown();
    }
}
