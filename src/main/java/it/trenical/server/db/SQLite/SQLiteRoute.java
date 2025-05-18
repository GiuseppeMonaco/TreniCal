package it.trenical.server.db.SQLite;

import it.trenical.common.RouteData;
import it.trenical.common.Station;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteRoute extends RouteData implements SQLiteTable {

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

    public SQLiteRoute(Station departureStation, Station arrivalStation, int distance) {
        super(departureStation, arrivalStation, distance);
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
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
        // TODO
    }
}
