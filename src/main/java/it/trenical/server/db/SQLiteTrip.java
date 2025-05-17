package it.trenical.server.db;

import it.trenical.common.Route;
import it.trenical.common.Train;
import it.trenical.common.Trip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public record SQLiteTrip(Train train, Route route, Calendar departureTime, int availableEconomySeats, int availableBusinessSeats) implements Trip, SQLiteTable {

    static private final String TABLE_NAME = "Trips";
    static private final int COLUMNS_NUMBER = 6;

    static private final String COLUMNS =
            "train INTEGER NOT NULL," +
            "departureTime INTEGER NOT NULL," +
            "departureStation TEXT NOT NULL," +
            "arrivalStation TEXT NOT NULL," +
            "availableEconomySeats INTEGER NOT NULL," +
            "availableBusinessSeats INTEGER NOT NULL," +
            "PRIMARY KEY (train,departureTime,departureStation,arrivalStation)," +
            "FOREIGN KEY (train) REFERENCES Trains(id)," +
            "FOREIGN KEY (departureStation,arrivalStation) REFERENCES Routes(departureStation,arrivalStation)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public Train getTrain() {
        return train;
    }

    @Override
    public Calendar getDepartureTime() {
        return departureTime;
    }

    @Override
    public Route getRoute() {
        return route;
    }

    @Override
    public int getAvailableEconomySeats() {
        return availableEconomySeats;
    }

    @Override
    public int getAvailableBusinessSeats() {
        return availableBusinessSeats;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setInt(1,train.getId());
        st.setLong(2,departureTime.getTimeInMillis());
        st.setString(3,route.getDepartureStation().getName());
        st.setString(4,route.getArrivalStation().getName());
        st.setInt(5,availableEconomySeats);
        st.setInt(COLUMNS_NUMBER,availableBusinessSeats);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
