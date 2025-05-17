package it.trenical.server.db;

import it.trenical.common.Station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLiteStation(String name, String address, String town, String province) implements Station, SQLiteTable {

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

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getTown() {
        return town;
    }

    @Override
    public String getProvince() {
        return province;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, name);
        st.setString(2, address);
        st.setString(3, town);
        st.setString(COLUMNS_NUMBER, province);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
