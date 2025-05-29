package it.trenical.server;

import it.trenical.server.connection.GrpcServerConnection;
import it.trenical.server.connection.ServerConnection;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.SQLiteConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public enum Server {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private ServerConnection serverConnection;
    private DatabaseConnection database;

    void initDatabase(DatabaseConnection db) {
        this.database = db;
    }
    void initServerConnection(ServerConnection sc) {
        this.serverConnection = sc;
        try {
            this.serverConnection.start();
        } catch (IOException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }
    }
    void closeDatabase() {
        database.close();
    }
    void waitUntilServerConnectionShutdown() {
        try {
            serverConnection.blockUntilShutdown();
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }
    public DatabaseConnection getDatabase() {
        return database;
    }

    public static void main(String[] args) throws Exception {
        Server server = Server.INSTANCE;
        server.initDatabase(SQLiteConnection.getInstance());
        server.initServerConnection(GrpcServerConnection.getInstance());

        server.waitUntilServerConnectionShutdown();
        server.closeDatabase();
    }
}
