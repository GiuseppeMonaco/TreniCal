package it.trenical.server.db.SQLite;

import it.trenical.common.UserData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLiteUser extends UserData implements SQLiteTable {
    
    static private final String TABLE_NAME = "Users";
    static private final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "email TEXT(100) NOT NULL, " +
            "password TEXT(100) NOT NULL, " +
            "PRIMARY KEY (email)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    public SQLiteUser(String email, String password) {
        super(email, password);
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, getEmail());
        st.setString(COLUMNS_NUMBER, getPassword());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }

    public boolean checkIfExists(DatabaseConnection db) throws SQLException {
        return getRecord(db) != null;
    }

    @Override
    public SQLiteUser getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE email=?;");
        st.setString(1, getEmail());
        ResultSet res = st.executeQuery();

        if (!res.next()) return null;
        return new SQLiteUser(res.getString(1),res.getString(2));
    }

}