package it.trenical.server.db;

import it.trenical.common.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

record SQLiteUser(String email, String password) implements User, SQLiteTable {

    static private final String TABLE_NAME = "Users";
    static private final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "email TEXT(100) NOT NULL, " +
            "password TEXT(100) NOT NULL, " +
            "PRIMARY KEY (email)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isFidelity() {
        return false;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, email);
        st.setString(COLUMNS_NUMBER, password);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}