package it.trenical.server.db.SQLite;

import it.trenical.common.Trip;
import it.trenical.server.db.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static it.trenical.common.testUtil.DataEquals.*;
import static it.trenical.common.testUtil.DataSamples.*;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteTest {

    private DatabaseConnection db;

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
    static void initialize() {
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

    @BeforeEach
    void newDatabase() {
        db = SQLiteConnection.getInstance(":memory:");
    }

    @AfterEach
    void cleanup() {
        db.close();
    }

    @Test
    void insertTest() {
        assertDoesNotThrow(() -> userDb.insertRecord(db));
        assertDoesNotThrow(() -> promotionDb.insertRecord(db));
        assertDoesNotThrow(() -> trainTypeDb.insertRecord(db));
        assertDoesNotThrow(() -> trainDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb2.insertRecord(db));
        assertDoesNotThrow(() -> routeDb.insertRecord(db));
        assertDoesNotThrow(() -> tripDb.insertRecord(db));
        assertDoesNotThrow(() -> ticketDb.insertRecord(db));
    }

    @Test
    void getTest() {

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

    @Test
    void getAllTest() {

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

        // GET ALL METHODS //
        assertDoesNotThrow(() -> assertFalse(userDb.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(promotionDb.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(trainTypeDb.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(trainDb.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(stationDb.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(stationDb2.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(routeDb.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(tripDb.getAllRecords(db).isEmpty()));
        assertDoesNotThrow(() -> assertFalse(ticketDb.getAllRecords(db).isEmpty()));
    }

    @Test
    void deleteTest() {

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

        // DELETE METHODS //
        assertDoesNotThrow(() -> ticketDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(ticketDb.getRecord(db)));
        assertDoesNotThrow(() -> tripDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(tripDb.getRecord(db)));
        assertDoesNotThrow(() -> routeDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(routeDb.getRecord(db)));
        assertDoesNotThrow(() -> stationDb2.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(stationDb2.getRecord(db)));
        assertDoesNotThrow(() -> stationDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(stationDb.getRecord(db)));
        assertDoesNotThrow(() -> trainDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(trainDb.getRecord(db)));
        assertDoesNotThrow(() -> trainTypeDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(trainTypeDb.getRecord(db)));
        assertDoesNotThrow(() -> promotionDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(promotionDb.getRecord(db)));
        assertDoesNotThrow(() -> userDb.deleteRecord(db));
        assertDoesNotThrow(() -> assertNull(userDb.getRecord(db)));
    }

    @Test
    void rollbackTest() {

        // INSERT METHODS //
        assertDoesNotThrow(() -> userDb.insertRecord(db));
        assertDoesNotThrow(() -> promotionDb.insertRecord(db));
        assertDoesNotThrow(() -> trainTypeDb.insertRecord(db));
        assertDoesNotThrow(() -> trainDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb2.insertRecord(db));
        assertDoesNotThrow(() -> routeDb.insertRecord(db));
        assertDoesNotThrow(() -> tripDb.insertRecord(db));

        int numEconomy = tripDb.getAvailableEconomySeats();
        int numBusiness = tripDb.getAvailableBusinessSeats();
        assertThrows(SQLException.class,() -> db.atomicTransaction(() -> {
            for (int i=0; i < numEconomy; i++)
                assertTrue(tripDb.increaseEconomySeats(db));
            for (int i=0; i < numBusiness-2; i++)
                assertTrue(tripDb.decreaseBusinessSeats(db));
            throw new SQLException();
        }));

        Trip newTrip = null;
        try {
            newTrip = tripDb.getRecord(db);
        } catch (SQLException e) {
            fail();
        }

        assertEquals(numEconomy,newTrip.getAvailableEconomySeats());
        assertEquals(numBusiness,newTrip.getAvailableBusinessSeats());

    }

    @Test
    void commitTest() {

        // INSERT METHODS //
        assertDoesNotThrow(() -> userDb.insertRecord(db));
        assertDoesNotThrow(() -> promotionDb.insertRecord(db));
        assertDoesNotThrow(() -> trainTypeDb.insertRecord(db));
        assertDoesNotThrow(() -> trainDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb.insertRecord(db));
        assertDoesNotThrow(() -> stationDb2.insertRecord(db));
        assertDoesNotThrow(() -> routeDb.insertRecord(db));
        assertDoesNotThrow(() -> tripDb.insertRecord(db));

        int numEconomy = tripDb.getAvailableEconomySeats();
        int numBusiness = tripDb.getAvailableBusinessSeats();
        assertDoesNotThrow(() -> db.atomicTransaction(() -> {
            for (int i=0; i < numEconomy; i++)
                assertTrue(tripDb.increaseEconomySeats(db));
            for (int i=0; i < numBusiness-2; i++)
                assertTrue(tripDb.decreaseBusinessSeats(db));
        }));

        Trip newTrip = null;
        try {
            newTrip = tripDb.getRecord(db);
        } catch (SQLException e) {
            fail();
        }

        assertEquals(numEconomy*2, newTrip.getAvailableEconomySeats());
        assertEquals(2, newTrip.getAvailableBusinessSeats());

    }

}