package it.trenical.server.db.SQLite;

import it.trenical.common.*;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

public class SQLiteTicket implements SQLiteTable<Ticket>, Ticket {

    static private final String TABLE_NAME = "Tickets";
    static private final int COLUMNS_NUMBER = 10;

    static private final String COLUMNS =
            "id INTEGER NOT NULL," +
            "userEmail TEXT NOT NULL," +
            "name TEXT(100) NOT NULL," +
            "surname TEXT(100) NOT NULL," +
            "price REAL NOT NULL," +
            "promotion INTEGER," +
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
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            SQLiteTable.getAllQuery(TABLE_NAME);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public int getColumnsNumber() {
        return COLUMNS_NUMBER;
    }

    @Override
    public String getInsertQuery() {
        return INSERT_QUERY;
    }

    @Override
    public String getAllQuery() {
        return ALL_QUERY;
    }

    private final Ticket data;

    public SQLiteTicket(Ticket data) {
        this.data = data;
    }

    public SQLiteTicket(int id, String userEmail) {
        this.data = TicketData.newBuilder(id, new UserData(userEmail)).build();
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
        if(getPromotion() != null) st.setString(6,getPromotion().getCode());
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
    public Ticket toRecord(ResultSet rs) throws SQLException {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(rs.getLong("tripDepartureTime"));
        Route r = new RouteData(
                StationData.newBuilder(rs.getString("tripDepartureStation")).build(),
                StationData.newBuilder(rs.getString("tripArrivalStation")).build()
        );

        TicketData.Builder b = TicketData.newBuilder(rs.getInt("id"),new UserData(rs.getString("userEmail")))
                .setName(rs.getString("name"))
                .setSurname(rs.getString("surname"))
                .setPrice(rs.getInt("price"))
                .setTrip(TripData.newBuilder(r)
                        .setTrain(TrainData.newBuilder(rs.getInt("tripTrain")).build())
                        .setDepartureTime(c)
                        .build()
                );

        String promotion = rs.getString("promotion");
        if(promotion != null) b.setPromotion(PromotionData.newBuilder(rs.getString("promotion")).build());

        return new SQLiteTicket(b.build());
    }

    @Override
    public Collection<Ticket> getSimilarRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(String.format("SELECT * FROM %s WHERE userEmail=?",TABLE_NAME));
        st.setString(1, getUser().getEmail());
        ResultSet rs = st.executeQuery();

        Collection<Ticket> ret = new LinkedList<>();
        while (rs.next()) ret.add(toRecord(rs));
        return ret;
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
