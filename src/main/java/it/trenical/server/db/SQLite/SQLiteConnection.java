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

    public static void main(String[] args) {
        SQLiteConnection db = SQLiteConnection.getInstance();

        try {
            SQLiteUser user = new SQLiteUser("mario.rossi@gmail.com", PasswordUtils.hashPassword("passwordbella123"));
            user.insertRecord(db);

            new SQLiteFidelityUser(user.getEmail()).insertRecord(db);

            new SQLitePromotion(PromotionData.newBuilder("exampleCode")
                    .setName("someName")
                    .setDescription("someDescription")
                    .setOnlyFidelityUser(true)
                    .setDiscount(0.8f)
                    .build()
            );

            SQLiteTrainType tt = new SQLiteTrainType(new TrainTypeData("Regionale",10));
            tt.insertRecord(db);

            SQLiteTrain train = new SQLiteTrain(TrainData.newBuilder(6)
                    .setType(tt)
                    .setEconomyCapacity(16)
                    .setBusinessCapacity(29)
                    .build()
            );
            train.insertRecord(db);

            SQLiteStation st1 = new SQLiteStation(StationData.newBuilder("Bisignano")
                    .setAddress("Via mammata 12")
                    .setProvince("Cosenza")
                    .setTown("Bisignano")
                    .build()
            );
            st1.insertRecord(db);

            SQLiteStation st2 = new SQLiteStation(StationData.newBuilder("Luzzi")
                    .setAddress("Via sorata 69")
                    .setProvince("Cosenza")
                    .setTown("Luzzi")
                    .build()
            );
            st2.insertRecord(db);

            SQLiteRoute r =  new SQLiteRoute(new RouteData(st1, st2, 150));
            r.insertRecord(db);

            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, 48);

            SQLiteTrip trip = new SQLiteTrip(TripData.newBuilder(r)
                    .setTrain(train)
                    .setDepartureTime(c)
                    .setAvailableEconomySeats(11)
                    .setAvailableBusinessSeats(19)
                    .build()
            );
            trip.insertRecord(db);

        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }
}
