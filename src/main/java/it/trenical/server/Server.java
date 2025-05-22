package it.trenical.server;

import it.trenical.server.connection.GrpcServerConnection;
import it.trenical.server.connection.ServerConnection;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.SQLiteConnection;

public class Server {

    private static final ServerConnection server = GrpcServerConnection.getInstance();
    private static final DatabaseConnection database = SQLiteConnection.getInstance();

    public static void main(String[] args) throws Exception {
        server.start();
        server.blockUntilShutdown();
    }
}
