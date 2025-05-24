package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SQLiteConnection implements DatabaseConnection {

    /**
     * "Singleton" classes
     * There will be one instance for every path
     */
    private static final Map<Path,SQLiteConnection> instances = new HashMap<>();

    private static final Logger logger = Logger.getLogger(SQLiteConnection.class.getName());

    private static final String DEFAULT_PATH = "./database.db";

    private Connection connection;
    private Statement statement;

    private SQLiteConnection(String dbPath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            statement = connection.createStatement();

            // Per abilitare il controllo delle chiavi esterne nelle tabelle
            statement.execute("PRAGMA foreign_keys = ON;");

            // Si inizializzano tutte le tabelle necessarie
            SQLiteUser.initTable(statement);
            SQLiteFidelityUser.initTable(statement);
            SQLitePromotion.initTable(statement);
            SQLiteTrainType.initTable(statement);
            SQLiteTrain.initTable(statement);
            SQLiteStation.initTable(statement);
            SQLiteRoute.initTable(statement);
            SQLiteTrip.initTable(statement);
            SQLiteTicket.initTable(statement);
            SQLitePaidTicket.initTable(statement);

        } catch (SQLException e) {
            if (e.getErrorCode() == 8) {
                logger.severe(e.getMessage());
            } else {
                logger.severe("Cannot initialize database connection. Please contact software developer.\n" + e.getErrorCode());
            }
            System.exit(-1);
        }
        logger.info(String.format("Database at %s initialized successfully", dbPath));
    }

    private static boolean areFilesEqual(Path p1, Path p2) {
        try {
            return Files.isSameFile(p1,p2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized SQLiteConnection getInstance(String path) {
        Path p = Path.of(path);
        if(!Files.exists(p)) {
            instances.put(p,new SQLiteConnection(path));
            return instances.get(p);
        }

        for(Path pp : instances.keySet()) {
            if(areFilesEqual(p,pp)) {
                return instances.get(pp);
            }
        }

        instances.put(p,new SQLiteConnection(path));
        return instances.get(p);
    }

    public static SQLiteConnection getInstance() {
        return getInstance(DEFAULT_PATH);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warning("Error closing SQLite database connection.\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SQLiteConnection db = SQLiteConnection.getInstance();

        try {
            new SQLiteUser("mario.rossi@gmail.com", "passwordbella123").insertRecord(db);
            new SQLiteFidelityUser("mario.rossi@gmail.com").insertRecord(db);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        try (ResultSet rs = db.statement.executeQuery("SELECT * FROM Users, FidelityUsers F WHERE Users.email = F.userEmail")) {

            while (rs.next()) {
                System.out.print("Email: " + rs.getString("email"));
                System.out.println(" --- Psw: " + rs.getString("password"));
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
}
