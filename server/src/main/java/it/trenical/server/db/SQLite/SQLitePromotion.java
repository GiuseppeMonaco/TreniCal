package it.trenical.server.db.SQLite;

import it.trenical.common.Promotion;
import it.trenical.common.PromotionData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLitePromotion implements SQLiteTable<Promotion>, Promotion {

    static final String TABLE_NAME = "Promotions";
    static private final int COLUMNS_NUMBER = 5;

    static private final String COLUMNS =
            "code TEXT(100) NOT NULL," +
            "name TEXT(100) NOT NULL," +
            "description TEXT(500) NOT NULL," +
            "onlyFidelity INTEGER(1) NOT NULL," +
            "discount REAL NOT NULL," +
            "PRIMARY KEY (code)";

    static private final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            SQLiteTable.getAllQuery(TABLE_NAME);

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s
            WHERE code=?;
            """,
            TABLE_NAME
    );

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
    public SQLitePromotion getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE code=?;");
        st.setString(1, getCode());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return new SQLitePromotion(toRecord(rs));
    }

    @Override
    public Promotion toRecord(ResultSet rs) throws SQLException {
        return PromotionData.newBuilder(rs.getString("code"))
                .setName(rs.getString("name"))
                .setDescription(rs.getString("description"))
                .setOnlyFidelityUser(rs.getBoolean("onlyFidelity"))
                .setDiscount(rs.getFloat("discount"))
                .build();
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1,getCode());
        st.executeUpdate();
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
