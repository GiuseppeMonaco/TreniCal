package it.trenical.server.db.SQLite;

import it.trenical.common.Station;
import it.trenical.common.StationData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLiteStation implements SQLiteTable<Station>, Station {

    static final String TABLE_NAME = "Stations";
    static private final int COLUMNS_NUMBER = 4;

    static private final String COLUMNS =
            "name TEXT(100) NOT NULL," +
            "address TEXT(100) NOT NULL," +
            "town TEXT(100) NOT NULL," +
            "province TEXT(100) NOT NULL," +
            "PRIMARY KEY (name)";

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

    private final Station data;

    public SQLiteStation(Station data) {
        this.data = data;
    }

    public SQLiteStation(String name) {
        this.data = StationData.newBuilder(name).build();
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
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE name=?;");
        st.setString(1, getName());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return new SQLiteStation(toRecord(rs));
    }

    @Override
    public Station toRecord(ResultSet rs) throws SQLException {
        return StationData.newBuilder(rs.getString("name"))
                .setAddress(rs.getString("address"))
                .setTown(rs.getString("town"))
                .setProvince(rs.getString("province"))
                .build();
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
