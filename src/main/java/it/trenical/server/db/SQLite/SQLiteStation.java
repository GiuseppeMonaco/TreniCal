package it.trenical.server.db.SQLite;

import it.trenical.common.Station;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteStation implements SQLiteTable<Station>, Station {

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

    private final Station data;

    public SQLiteStation(Station data) {
        this.data = data;
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, data.getName());
        st.setString(2, data.getAddress());
        st.setString(3, data.getTown());
        st.setString(COLUMNS_NUMBER, data.getProvince());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("updateRecord"); // TODO
    }

    @Override
    public SQLiteStation getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord"); // TODO
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public String getAddress() {
        return data.getAddress();
    }

    @Override
    public String getTown() {
        return data.getTown();
    }

    @Override
    public String getProvince() {
        return data.getProvince();
    }
}
