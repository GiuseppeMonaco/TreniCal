package it.trenical.server;

import it.trenical.common.*;
import it.trenical.server.connection.GrpcServerConnection;
import it.trenical.server.connection.ServerConnection;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.*;
import it.trenical.server.db.exceptions.ForeignKeyException;
import it.trenical.server.db.exceptions.PrimaryKeyException;
import it.trenical.server.observer.*;
import it.trenical.server.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

// Singleton class
public enum Server {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private ServerConnection serverConnection;
    private DatabaseConnection database;
    private Scheduler scheduler;

    public void initDatabase(DatabaseConnection db) {
        this.database = db;
    }
    public void initServerConnection(ServerConnection sc) {
        this.serverConnection = sc;
        try {
            this.serverConnection.start();
        } catch (IOException e) {
            logger.error(e.getMessage());
            if(database != null) closeDatabase();
            System.exit(-1);
        }
    }
    public void initScheduler() {
        if(scheduler == null) scheduler = Scheduler.INSTANCE;
        scheduler.startSchedules();
    }
    public void stopScheduler() {
        scheduler.shutdown();
    }
    public void closeDatabase() {
        database.close();
    }
    public void waitUntilServerConnectionShutdown() {
        try {
            serverConnection.blockUntilShutdown();
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }
    public DatabaseConnection getDatabase() {
        return database;
    }


    // DATA CACHES //
    private final Collection<TrainType> trainTypesCache = new LinkedList<>();
    private final Collection<Train> trainsCache = new LinkedList<>();
    private final Collection<Station> stationsCache = new LinkedList<>();
    private final Collection<Route> routesCache = new LinkedList<>();
    private final Collection<Trip> tripsCache = new LinkedList<>();
    private final Collection<Promotion> promotionsCache = new LinkedList<>();
    private final Collection<User> usersCache = new LinkedList<>();
    private final Collection<Ticket> ticketsCache = new LinkedList<>();

    // DATA SUBJECTS //
    public final TrainTypesCache.Subject trainTypesCacheObs = new TrainTypesCacheSubject();
    public final TrainsCache.Subject trainsCacheObs = new TrainsCacheSubject();
    public final StationsCache.Subject stationsCacheObs = new StationsCacheSubject();
    public final RoutesCache.Subject routesCacheObs = new RoutesCacheSubject();
    public final TripsCache.Subject tripsCacheObs = new TripsCacheSubject();
    public final PromotionsCache.Subject promotionsCacheObs = new PromotionsCacheSubject();
    public final UsersCache.Subject usersCacheObs = new UsersCacheSubject();
    public final TicketsCache.Subject ticketsCacheObs = new TicketsCacheSubject();

    // DATA CACHES GETTERS //
    public Collection<TrainType> getTrainTypesCache() {
        return trainTypesCache;
    }
    public Collection<Train> getTrainsCache() {
        return trainsCache;
    }
    public Collection<Station> getStationsCache() {
        return stationsCache;
    }
    public Collection<Route> getRoutesCache() {
        return routesCache;
    }
    public Collection<Trip> getTripsCache() {
        return tripsCache;
    }
    public Collection<Promotion> getPromotionsCache() {
        return promotionsCache;
    }
    public Collection<User> getUsersCache() {
        return usersCache;
    }
    public Collection<Ticket> getTicketsCache() {
        return ticketsCache;
    }

    // CACHES UPDATE METHODS //
    public void updateTrainTypesCache() {
        trainTypesCache.clear();
        try {
            trainTypesCache.addAll(new SQLiteTrainType("").getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        trainTypesCacheObs.notifyObs();
    }
    public void updateTrainsCache() {
        trainsCache.clear();
        try {
            trainsCache.addAll(new SQLiteTrain(null).getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        trainsCacheObs.notifyObs();
    }
    public void updateStationsCache() {
        stationsCache.clear();
        try {
            stationsCache.addAll(new SQLiteStation("").getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        stationsCacheObs.notifyObs();
    }
    public void updateRoutesCache() {
        routesCache.clear();
        try {
            routesCache.addAll(new SQLiteRoute(null).getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        routesCacheObs.notifyObs();
    }
    public void updateTripsCache() {
        tripsCache.clear();
        try {
            tripsCache.addAll(new SQLiteTrip(null).getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        tripsCacheObs.notifyObs();
    }
    public void updatePromotionsCache() {
        promotionsCache.clear();
        try {
            promotionsCache.addAll(new SQLitePromotion(null).getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        promotionsCacheObs.notifyObs();
    }
    public void updateUsersCache() {
        usersCache.clear();
        try {
            usersCache.addAll(new SQLiteUser(null).getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        usersCacheObs.notifyObs();
    }
    public void updateTicketsCache() {
        ticketsCache.clear();
        try {
            ticketsCache.addAll(new SQLiteTicket(null).getAllRecords(database));
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        ticketsCacheObs.notifyObs();
    }

    // CREATE METHODS //
    public void createTrainType(TrainType trainType) throws PrimaryKeyException {
        try {
            new SQLiteTrainType(trainType).insertRecord(database);
        } catch (SQLException e) {
            handleSQLExceptionWithoutForeignKey(e,"trainType");
        }
        updateTrainTypesCache();
    }
    public void createTrain(Train train) throws PrimaryKeyException, ForeignKeyException {
        try {
            new SQLiteTrain(train).insertRecord(database);
        } catch (SQLException e) {
            handleSQLException(e,"train");
        }
        updateTrainsCache();
    }
    public void createStation(Station station) throws PrimaryKeyException {
        try {
            new SQLiteStation(station).insertRecord(database);
        } catch (SQLException e) {
            handleSQLExceptionWithoutForeignKey(e,"station");
        }
        updateStationsCache();
    }
    public void createRoute(Route route) throws PrimaryKeyException, ForeignKeyException {
        try {
            new SQLiteRoute(route).insertRecord(database);
        } catch (SQLException e) {
            handleSQLException(e,"route");
        }
        updateRoutesCache();
    }
    public void createTrip(Trip trip) throws PrimaryKeyException, ForeignKeyException {
        try {
            new SQLiteTrip(trip).insertRecord(database);
        } catch (SQLException e) {
            handleSQLException(e,"trip");
        }
        updateTripsCache();
    }
    public void createPromotion(Promotion promotion) throws PrimaryKeyException {
        try {
            new SQLitePromotion(promotion).insertRecord(database);
        } catch (SQLException e) {
            handleSQLExceptionWithoutForeignKey(e,"promotion");
        }
        updatePromotionsCache();
    }
    public void createUser(User user) throws PrimaryKeyException {
        try {
            new SQLiteUser(user).insertRecord(database);
        } catch (SQLException e) {
            handleSQLExceptionWithoutForeignKey(e,"user");
        }
        updateUsersCache();
    }
    public void createTicket(Ticket ticket) throws PrimaryKeyException, ForeignKeyException {
        try {
            new SQLiteTicket(ticket).insertRecord(database);
        } catch (SQLException e) {
            handleSQLException(e,"ticket");
        }
        updateTicketsCache();
    }
    private void handleSQLException(SQLException e, String obj) throws PrimaryKeyException, ForeignKeyException {
        if (e.getMessage().contains("CONSTRAINT_PRIMARYKEY"))
            throw new PrimaryKeyException(String.format("Given %s already exists in the database",obj));
        else if (e.getMessage().contains("CONSTRAINT_FOREIGNKEY"))
            throw new ForeignKeyException(String.format("Given %s violates a foreign key constraint",obj));
        else
            logger.error(e.getMessage());
    }
    private void handleSQLExceptionWithoutForeignKey(SQLException e, String obj) throws PrimaryKeyException {
        if (e.getMessage().contains("CONSTRAINT_PRIMARYKEY"))
            throw new PrimaryKeyException(String.format("Given %s already exists in the database",obj));
        else
            logger.error(e.getMessage());
    }

    // UPDATE METHODS //
    public void updateTrainType(TrainType trainType) {
        try {
            new SQLiteTrainType(trainType).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTrainTypesCache();
    }
    public void updateTrain(Train train) {
        try {
            new SQLiteTrain(train).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTrainsCache();
    }
    public void updateStation(Station station) {
        try {
            new SQLiteStation(station).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateStationsCache();
    }
    public void updateRoute(Route route) {
        try {
            new SQLiteRoute(route).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateRoutesCache();
    }
    public void updateTrip(Trip trip) {
        try {
            new SQLiteTrip(trip).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTripsCache();
    }
    public void updatePromotion(Promotion promotion) {
        try {
            new SQLitePromotion(promotion).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updatePromotionsCache();
    }
    public void updateUser(User user) {
        try {
            new SQLiteUser(user).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateUsersCache();
    }
    public void updateTicket(Ticket ticket) {
        try {
            new SQLiteTicket(ticket).updateRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTicketsCache();
    }

    // DELETE METHODS //
    public void deleteTrainType(TrainType trainType) {
        try {
            new SQLiteTrainType(trainType).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTrainTypesCache();
        updateTrainsCache();
        updateTripsCache();
        updateTicketsCache();
    }
    public void deleteTrain(Train train) {
        try {
            new SQLiteTrain(train).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTrainsCache();
        updateTripsCache();
        updateTicketsCache();
    }
    public void deleteStation(Station station) {
        try {
            new SQLiteStation(station).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateStationsCache();
        updateRoutesCache();
        updateTripsCache();
        updateTicketsCache();
    }
    public void deleteRoute(Route route) {
        try {
            new SQLiteRoute(route).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateRoutesCache();
        updateTripsCache();
        updateTicketsCache();
    }
    public void deleteTrip(Trip trip) {
        try {
            new SQLiteTrip(trip).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTripsCache();
        updateTicketsCache();
    }
    public void deletePromotion(Promotion promotion) {
        try {
            new SQLitePromotion(promotion).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updatePromotionsCache();
    }
    public void deleteUser(User user) {
        try {
            new SQLiteUser(user).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateUsersCache();
        updateTicketsCache();
    }
    public void deleteTicket(Ticket ticket) {
        try {
            new SQLiteTicket(ticket).deleteRecord(database);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        updateTicketsCache();
    }

    public static void main(String[] args) throws Exception {
        Server server = Server.INSTANCE;
        server.initDatabase(SQLiteConnection.getInstance());
        server.initScheduler();
        server.initServerConnection(GrpcServerConnection.getInstance());

        server.waitUntilServerConnectionShutdown();
        server.stopScheduler();
        server.closeDatabase();
    }
}
