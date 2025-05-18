package it.trenical.server.db.SQLite;

import it.trenical.common.*;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTicket extends TicketData implements SQLiteTable {

    static private final String TABLE_NAME = "Tickets";
    static private final int COLUMNS_NUMBER = 10;

    static private final String COLUMNS =
            "id INTEGER NOT NULL," +
            "userEmail TEXT NOT NULL," +
            "name TEXT(100) NOT NULL," +
            "surname TEXT(100) NOT NULL," +
            "price REAL NOT NULL," +
            "promotion INTEGER NOT NULL," +
            "tripTrain INTEGER NOT NULL," +
            "tripDepartureTime INTEGER NOT NULL," +
            "tripDepartureStation TEXT NOT NULL," +
            "tripArrivalStation TEXT NOT NULL," +
            "PRIMARY KEY (id,userEmail)," +
            "FOREIGN KEY (userEmail) REFERENCES Users(email)," +
            "FOREIGN KEY (promotion) REFERENCES Promotions(code)," +
            "FOREIGN KEY (tripTrain,tripDepartureTime,tripDepartureStation,tripArrivalStation) " +
            "REFERENCES Trips(train,departureTime,departureStation,arrivalStation)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    public static Builder newBuilder(int id, User user) {
        return new Builder(id, user);
    }

    private SQLiteTicket(Builder builder) {
        super(builder);
    }

    public static class Builder extends TicketData.Builder {
        private Builder(int id, User user) {
            super(id, user);
        }

        @Override
        public SQLiteTicket build() {
            return (SQLiteTicket) super.build();
        }
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setInt(1, getId());
        st.setString(2, getUser().getEmail());
        st.setString(3, getName());
        st.setString(4, getSurname());
        st.setFloat(5, getPrice());
        st.setString(6,getPromotion().getCode());
        st.setInt(7,getTrip().getTrain().getId());
        st.setLong(8,getTrip().getDepartureTime().getTimeInMillis());
        st.setString(9,getTrip().getRoute().getDepartureStation().getName());
        st.setString(COLUMNS_NUMBER,getTrip().getRoute().getArrivalStation().getName());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
