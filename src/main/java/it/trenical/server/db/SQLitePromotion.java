package it.trenical.server.db;

import it.trenical.common.Promotion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLitePromotion(String code, String name, String description, boolean onlyFidelityUser, float discount) implements Promotion, SQLiteTable {

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

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOnlyFidelityUser() {
        return onlyFidelityUser;
    }

    @Override
    public float getDiscount() {
        return discount;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, code);
        st.setString(2, name);
        st.setString(3, description);
        st.setBoolean(4, onlyFidelityUser);
        st.setDouble(COLUMNS_NUMBER, discount);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
