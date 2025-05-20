package it.trenical.server.db.SQLite;

import it.trenical.common.*;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTicket implements SQLiteTable<Ticket>, Ticket {

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

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    private final Ticket data;

    public SQLiteTicket(Ticket data) {
        this.data = data;
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
        throw new UnsupportedOperationException("updateRecord"); // TODO
    }

    @Override
    public SQLiteTicket getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord"); // TODO
    }

    @Override
    public int getId() {
        return data.getId();
    }

    @Override
    public User getUser() {
        return data.getUser();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public String getSurname() {
        return data.getSurname();
    }

    @Override
    public float getPrice() {
        return data.getPrice();
    }

    @Override
    public Promotion getPromotion() {
        return data.getPromotion();
    }

    @Override
    public Trip getTrip() {
        return data.getTrip();
    }

    @Override
    public boolean isPaid() {
        return data.isPaid();
    }
}
