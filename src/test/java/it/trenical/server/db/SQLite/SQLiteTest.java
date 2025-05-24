package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.trenical.common.DataEquals.*;
import static org.junit.jupiter.api.Assertions.*;
import static it.trenical.common.DataSamples.*;

class SQLiteTest {

    static private final Logger logger = LoggerFactory.getLogger(SQLiteTest.class);

    static private Path dbTempPath;
    static private DatabaseConnection db;

    static private SQLiteUser userDb;
    static private SQLitePromotion promotionDb;
    static private SQLiteTrainType trainTypeDb;
    static private SQLiteTrain trainDb;
    static private SQLiteStation stationDb;
    static private SQLiteStation stationDb2;
    static private SQLiteRoute routeDb;
    static private SQLiteTrip tripDb;
    static private SQLiteTicket ticketDb;

    @BeforeAll
    static void initialize() throws IOException {
        dbTempPath = Files.createTempFile("test",".db");
        db = SQLiteConnection.getInstance(dbTempPath.toString());
        userDb = new SQLiteUser(user);
        promotionDb = new SQLitePromotion(promotion);
        trainTypeDb = new SQLiteTrainType(trainType);
        trainDb = new SQLiteTrain(train);
        stationDb = new SQLiteStation(station);
        stationDb2 = new SQLiteStation(station2);
        routeDb = new SQLiteRoute(route);
        tripDb = new SQLiteTrip(trip);
        ticketDb = new SQLiteTicket(ticket);
    }

    @AfterAll
    static void cleanup() {
        db.close();
        try {
            Files.deleteIfExists(dbTempPath);
            logger.info("Database at {} deleted successfully", dbTempPath);
        } catch (IOException e) {
            logger.warn("Cannot delete test database file at {}.\n{}", dbTempPath, e.getMessage());
        }
    }

    @Test
    void databaseTest() {

        // INSERT METHODS //
        assertDoesNotThrow(() -> userDb.insertRecord(db));
        assertDoesNotThrow(() -> promotionDb.insertRecord(db));
        assertDoesNotThrow(() -> trainTypeDb.insertRecord(db));
        assertDoesNotThrow(() -> trainDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb2.insertRecord(db));
        assertDoesNotThrow(() -> routeDb.insertRecord(db));
        assertDoesNotThrow(() -> tripDb.insertRecord(db));
        assertDoesNotThrow(() -> ticketDb.insertRecord(db));

        // GET METHODS //
        assertDoesNotThrow(() -> assertUserEquals(userDb,userDb.getRecord(db)));
        assertDoesNotThrow(() -> assertPromotionEquals(promotionDb, promotionDb.getRecord(db)));
        assertDoesNotThrow(() -> assertTrainTypeEquals(trainTypeDb, trainTypeDb.getRecord(db)));
        assertDoesNotThrow(() -> assertTrainEquals(trainDb, trainDb.getRecord(db)));
        assertDoesNotThrow(() -> assertStationEquals(stationDb, stationDb.getRecord(db)));
        assertDoesNotThrow(() -> assertStationEquals(stationDb2, stationDb2.getRecord(db)));
        assertDoesNotThrow(() -> assertRouteEquals(routeDb, routeDb.getRecord(db)));
        assertDoesNotThrow(() -> assertTripEquals(tripDb, tripDb.getRecord(db)));
        assertDoesNotThrow(() -> assertTicketEquals(ticketDb, ticketDb.getRecord(db)));

    }

}