package it.trenical.server.db.SQLite;

import it.trenical.common.Route;
import it.trenical.common.RouteData;
import it.trenical.common.Station;
import it.trenical.common.StationData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLiteRoute implements SQLiteTable<Route>, Route {

    static private final String TABLE_NAME = "Routes";
    static private final int COLUMNS_NUMBER = 3;

    static private final String COLUMNS =
            "departureStation TEXT NOT NULL," +
            "arrivalStation TEXT NOT NULL," +
            "distance INTEGER NOT NULL," +
            "PRIMARY KEY (departureStation,arrivalStation)," +
            "FOREIGN KEY (departureStation) REFERENCES Stations(name)," +
            "FOREIGN KEY (arrivalStation) REFERENCES Stations(name)";

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
        throw new UnsupportedOperationException("getRecord"); // TODO
    }

    @Override
    public Route toRecord(ResultSet rs) throws SQLException {
        return new SQLiteRoute(
                StationData.newBuilder(rs.getString("departureStation")).build(),
                StationData.newBuilder(rs.getString("arrivalStation")).build(),
                rs.getInt("distance")
        );
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
