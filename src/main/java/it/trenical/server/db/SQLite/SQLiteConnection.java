package it.trenical.server.db.SQLite;

import it.trenical.common.*;
import it.trenical.server.auth.PasswordUtils;
import it.trenical.server.db.DatabaseConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLiteConnection implements DatabaseConnection {

    /**
     * "Singleton" classes
     * There will be one instance for every path
     */
    private static final Map<Path,SQLiteConnection> instances = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SQLiteConnection.class);

    private static final String DEFAULT_PATH = "./database.db";

    private Connection connection;

    private SQLiteConnection(String dbPath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement statement = connection.createStatement();

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
                logger.error(e.getMessage());
            } else {
                logger.error("Cannot initialize database connection. Please contact software developer: {}", e.getMessage());
            }
            System.exit(-1);
        }
        logger.info("Database at {} initialized successfully", dbPath);
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
            logger.warn("Error closing SQLite database connection.\n{}", e.getMessage());
        }
    }

    @Override
    public void atomicTransaction(SQLConsumer query) throws SQLException {
        boolean currentAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            query.accept();
            connection.commit();
        } catch (SQLException e) {
            logger.warn("Error executing transaction, executing rollback: {}",e.getMessage());
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(currentAutoCommit);
        }
    }
}
