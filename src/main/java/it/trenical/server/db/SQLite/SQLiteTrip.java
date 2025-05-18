package it.trenical.server.db.SQLite;

import it.trenical.common.TripData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTrip extends TripData implements SQLiteTable {

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

    public static Builder newBuilder() {
        return new Builder();
    }

    private SQLiteTrip(Builder builder) {
        super(builder);
    }

    public static class Builder extends TripData.Builder {
        @Override
        public SQLiteTrip build() {
            return (SQLiteTrip) super.build();
        }
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setInt(1,getTrain().getId());
        st.setLong(2,getDepartureTime().getTimeInMillis());
        st.setString(3,getRoute().getDepartureStation().getName());
        st.setString(4,getRoute().getArrivalStation().getName());
        st.setInt(5,getAvailableEconomySeats());
        st.setInt(COLUMNS_NUMBER,getAvailableBusinessSeats());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
