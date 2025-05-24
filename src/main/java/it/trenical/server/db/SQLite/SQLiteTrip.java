package it.trenical.server.db.SQLite;

import it.trenical.common.*;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

public class SQLiteTrip implements SQLiteTable<Trip>, Trip {

    static final String TABLE_NAME = "Trips";
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
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            String.format("""
                    SELECT
                      tr.train,
                      tr.departureTime,
                      tr.departureStation,
                      s1.address AS departure_address,
                      s1.town AS departure_town,
                      s1.province AS departure_province,
                      tr.arrivalStation,
                      s2.address AS arrival_address,
                      s2.town AS arrival_town,
                      s2.province AS arrival_province,
                      r.distance,
                      tr.availableEconomySeats,
                      tr.availableBusinessSeats,
                      tt.name AS train_type,
                      tt.price AS type_price,
                      t.economyCapacity,
                      t.businessCapacity
                    FROM %s tr, %s t, %s tt, %s r, %s s1, %s s2
                    WHERE tr.train = t.id
                      AND tr.departureStation = r.departureStation
                      AND tr.arrivalStation = r.arrivalStation
                      AND r.departureStation = s1.name
                      AND r.arrivalStation = s2.name
                      AND t.type = tt.name
                    """,
                    TABLE_NAME,
                    SQLiteTrain.TABLE_NAME,
                    SQLiteTrainType.TABLE_NAME,
                    SQLiteRoute.TABLE_NAME,
                    SQLiteStation.TABLE_NAME,
                    SQLiteStation.TABLE_NAME
                    );

    static private final String GET_QUERY = String.format("""
            %s AND
            tr.train=? AND
            tr.departureTime=? AND
            tr.departureStation=? AND
            tr.arrivalStation=?;
            """,
            ALL_QUERY);

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

    private final Trip data;

    public SQLiteTrip(Trip data) {
        this.data = data;
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
        throw new UnsupportedOperationException("updateRecord"); // TODO
    }

    @Override
    public SQLiteTrip getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        if (getTrain() != null) st.setInt(1, getTrain().getId());
        if (getDepartureTime() != null) st.setLong(2, getDepartureTime().getTimeInMillis());
        st.setString(3, getRoute().getDepartureStation().getName());
        st.setString(4, getRoute().getArrivalStation().getName());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;

        return new SQLiteTrip(toRecord(rs));
    }

    @Override
    public Trip toRecord(ResultSet rs) throws SQLException {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(rs.getLong("departureTime"));
        Route r = new RouteData(
                StationData.newBuilder(rs.getString("departureStation"))
                        .setAddress(rs.getString("departure_address"))
                        .setProvince(rs.getString("departure_province"))
                        .setTown(rs.getString("departure_town"))
                        .build(),
                StationData.newBuilder(rs.getString("arrivalStation"))
                        .setAddress(rs.getString("arrival_address"))
                        .setProvince(rs.getString("arrival_province"))
                        .setTown(rs.getString("arrival_town"))
                        .build(),
                rs.getInt("distance")
        );

        Train t = TrainData.newBuilder(rs.getInt("train"))
                .setType(new TrainTypeData(rs.getString("train_type"),rs.getFloat("type_price")))
                .setEconomyCapacity(rs.getInt("economyCapacity"))
                .setBusinessCapacity(rs.getInt("businessCapacity"))
                .build();

        return TripData.newBuilder(r)
                .setTrain(t)
                .setDepartureTime(c)
                .setAvailableEconomySeats(rs.getInt("availableEconomySeats"))
                .setAvailableBusinessSeats(rs.getInt("availableBusinessSeats"))
                .build();
    }

    static private final String SIMILAR_QUERY = String.format("SELECT * FROM %s " +
            "WHERE (? OR train=?) " +
            "AND (? OR departureTime=?) " +
            "AND (? OR departureStation=?) " +
            "AND (? OR arrivalStation=?);",
            TABLE_NAME
    );

    @Override
    public Collection<Trip> getSimilarRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(SIMILAR_QUERY);
        st.setBoolean(1,true);
        st.setBoolean(3,true);
        st.setBoolean(5,true);
        st.setBoolean(7,true);
        if(getTrain() != null) {
            st.setBoolean(1,false);
            st.setInt(2,getTrain().getId());
        }
        if(getDepartureTime() != null) {
            st.setBoolean(3,false);
            st.setLong(4, getDepartureTime().getTimeInMillis());
        }
        if(getRoute() != null) {
            Route r = getRoute();
            if(r.getDepartureStation() != null && !r.getDepartureStation().getName().isEmpty()) {
                st.setBoolean(5,false);
                st.setString(6, r.getDepartureStation().getName());
            }
            if(r.getArrivalStation() != null && !r.getDepartureStation().getName().isEmpty()) {
                st.setBoolean(7,false);
                st.setString(8, r.getArrivalStation().getName());
            }
        }
        ResultSet rs = st.executeQuery();

        Collection<Trip> ret = new LinkedList<>();
        while (rs.next()) ret.add(toRecord(rs));
        return ret;
    }

    @Override
    public Train getTrain() {
        return data.getTrain();
    }

    @Override
    public Calendar getDepartureTime() {
        return data.getDepartureTime();
    }

    @Override
    public Route getRoute() {
        return data.getRoute();
    }

    @Override
    public int getAvailableEconomySeats() {
        return data.getAvailableEconomySeats();
    }

    @Override
    public int getAvailableBusinessSeats() {
        return data.getAvailableBusinessSeats();
    }
}
