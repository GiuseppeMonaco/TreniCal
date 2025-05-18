package it.trenical.server.db.SQLite;

import it.trenical.common.StationData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteStation extends StationData implements SQLiteTable {

    static private final String TABLE_NAME = "Stations";
    static private final int COLUMNS_NUMBER = 4;

    static private final String COLUMNS =
            "name TEXT(100) NOT NULL," +
            "address TEXT(100) NOT NULL," +
            "town TEXT(100) NOT NULL," +
            "province TEXT(100) NOT NULL," +
            "PRIMARY KEY (name)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    public static Builder newBuilder() {
        return new Builder();
    }

    private SQLiteStation(Builder builder) {
        super(builder);
    }

    public static class Builder extends StationData.Builder {
        @Override
        public SQLiteStation build() {
            return (SQLiteStation) super.build();
        }
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, getName());
        st.setString(2, getAddress());
        st.setString(3, getTown());
        st.setString(COLUMNS_NUMBER, getProvince());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
