package it.trenical.server.db;

import it.trenical.common.Promotion;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;
import it.trenical.common.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLiteTicket(int id, User user, String name, String surname, float price, Trip trip, Promotion promotion, boolean isPaid) implements Ticket, SQLiteTable {

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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public Promotion getPromotion() {
        return promotion;
    }

    @Override
    public Trip getTrip() {
        return trip;
    }

    @Override
    public boolean isPaid() {
        return isPaid;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setInt(1, id);
        st.setString(2, user.getEmail());
        st.setString(3, name);
        st.setString(4, surname);
        st.setFloat(5, price);
        st.setString(6,promotion.getCode());
        st.setInt(7,trip.getTrain().getId());
        st.setLong(8,trip.getDepartureTime().getTimeInMillis());
        st.setString(9,trip.getRoute().getDepartureStation().getName());
        st.setString(COLUMNS_NUMBER,trip.getRoute().getArrivalStation().getName());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
