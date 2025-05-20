package it.trenical.server.db.SQLite;

import it.trenical.common.Promotion;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLitePromotion implements SQLiteTable<Promotion>, Promotion {

    static private final String TABLE_NAME = "Promotions";
    static private final int COLUMNS_NUMBER = 5;

    static private final String COLUMNS =
            "code TEXT(100) NOT NULL," +
            "name TEXT(100) NOT NULL," +
            "description TEXT(500) NOT NULL," +
            "onlyFidelity INTEGER(1) NOT NULL," +
            "discount REAL NOT NULL," +
            "PRIMARY KEY (code)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    private final Promotion data;

    public SQLitePromotion(Promotion data) {
        this.data = data;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, getCode());
        st.setString(2, getName());
        st.setString(3, getDescription());
        st.setBoolean(4, isOnlyFidelityUser());
        st.setDouble(COLUMNS_NUMBER, getDiscount());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("updateRecord"); // TODO
    }

    @Override
    public SQLitePromotion getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord"); // TODO
    }

    @Override
    public String getCode() {
        return data.getCode();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public String getDescription() {
        return data.getDescription();
    }

    @Override
    public boolean isOnlyFidelityUser() {
        return data.isOnlyFidelityUser();
    }

    @Override
    public float getDiscount() {
        return data.getDiscount();
    }
}
