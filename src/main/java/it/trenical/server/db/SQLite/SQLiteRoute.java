package it.trenical.server.db.SQLite;

import it.trenical.common.Route;
import it.trenical.common.RouteData;
import it.trenical.common.Station;
import it.trenical.common.StationData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLiteRoute implements SQLiteTable<Route>, Route {

    static final String TABLE_NAME = "Routes";
    static private final int COLUMNS_NUMBER = 3;

    static private final String COLUMNS =
            "departureStation TEXT NOT NULL," +
            "arrivalStation TEXT NOT NULL," +
            "distance INTEGER NOT NULL," +
            "PRIMARY KEY (departureStation,arrivalStation)," +
            "FOREIGN KEY (departureStation) REFERENCES Stations(name) ON DELETE CASCADE," +
            "FOREIGN KEY (arrivalStation) REFERENCES Stations(name) ON DELETE CASCADE";

    static private final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            String.format("""
                            SELECT
                              r.departureStation,
                              s1.address AS departure_address,
                              s1.town AS departure_town,
                              s1.province AS departure_province,
                              r.arrivalStation,
                              s2.address AS arrival_address,
                              s2.town AS arrival_town,
                              s2.province AS arrival_province,
                              r.distance
                            FROM %s r, %s s1, %s s2
                            WHERE r.departureStation = s1.name
                              AND r.arrivalStation = s2.name
                            """,
                            TABLE_NAME,
                            SQLiteStation.TABLE_NAME,
                            SQLiteStation.TABLE_NAME);

    static private final String GET_QUERY = String.format("""
            %s AND
            r.departureStation=? AND
            r.arrivalStation=?;
            """,
            ALL_QUERY);

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s WHERE
            departureStation=? AND
            arrivalStation=?;
            """,
            TABLE_NAME
    );

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

    private final Route data;

    public SQLiteRoute(Route data) {
        this.data = data;
    }

    public SQLiteRoute(Station departureStation, Station arrivalStation, int distance) {
        this(new RouteData(departureStation,arrivalStation,distance));
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, getDepartureStation().getName());
        st.setString(2, getArrivalStation().getName());
        st.setInt(COLUMNS_NUMBER, getDistance());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("updateRecord"); // TODO
    }

    @Override
    public SQLiteRoute getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setString(1, getDepartureStation().getName());
        st.setString(2, getArrivalStation().getName());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return new SQLiteRoute(toRecord(rs));
    }

    @Override
    public Route toRecord(ResultSet rs) throws SQLException {
        return new RouteData(
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
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1, getDepartureStation().getName());
        st.setString(2, getArrivalStation().getName());
        st.executeUpdate();
    }

    @Override
    public Station getDepartureStation() {
        return data.getDepartureStation();
    }

    @Override
    public Station getArrivalStation() {
        return data.getArrivalStation();
    }

    @Override
    public int getDistance() {
        return data.getDistance();
    }
}
