package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;

import java.sql.*;
import java.util.Collection;

public record SQLiteFidelityUser(String userEmail) implements SQLiteTable<SQLiteFidelityUser> {

    static private final String TABLE_NAME = "FidelityUsers";
    static private final int COLUMNS_NUMBER = 1;

    static private final String COLUMNS =
            "userEmail TEXT NOT NULL, " +
            "PRIMARY KEY (userEmail)," +
            "FOREIGN KEY (userEmail) REFERENCES Users(email)";

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

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(COLUMNS_NUMBER,userEmail);
        st.executeUpdate();
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        SQLiteTable.super.deleteRecord(db);
    }

    @Override
    public Collection<SQLiteFidelityUser> getSimilarRecords(DatabaseConnection db) throws SQLException {
        return SQLiteTable.super.getSimilarRecords(db);
    }

    @Override
    public SQLiteFidelityUser getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord"); // TODO
    }

    @Override
    public SQLiteFidelityUser toRecord(ResultSet rs) throws SQLException{
        return new SQLiteFidelityUser(rs.getString("userEmail"));
    }
}
