package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLiteFidelityUser(String userEmail) implements SQLiteTable {

    static private final String TABLE_NAME = "FidelityUsers";
    static private final int COLUMNS_NUMBER = 1;

    static private final String COLUMNS =
            "userEmail TEXT NOT NULL, " +
            "PRIMARY KEY (userEmail)," +
            "FOREIGN KEY (userEmail) REFERENCES Users(email)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(COLUMNS_NUMBER,userEmail);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
