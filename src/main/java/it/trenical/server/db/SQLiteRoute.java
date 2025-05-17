package it.trenical.server.db;

import it.trenical.common.Route;
import it.trenical.common.Station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLiteRoute(Station departureStation, Station arrivalStation, int distance) implements Route, SQLiteTable {

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
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public Station getDepartureStation() {
        return departureStation;
    }

    @Override
    public Station getArrivalStation() {
        return arrivalStation;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, departureStation.getName());
        st.setString(2, arrivalStation.getName());
        st.setInt(COLUMNS_NUMBER, distance);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
