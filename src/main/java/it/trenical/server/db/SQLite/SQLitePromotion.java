package it.trenical.server.db.SQLite;

import it.trenical.common.PromotionData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLitePromotion extends PromotionData implements SQLiteTable {

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

    public static Builder newBuilder(String code) {
        return new Builder(code);
    }

    private SQLitePromotion(Builder builder) {
        super(builder);
    }

    public static class Builder extends PromotionData.Builder {
        private Builder(String code) {
            super(code);
        }

        @Override
        public SQLitePromotion build() {
            return (SQLitePromotion) super.build();
        }
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
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
        // TODO
    }

    @Override
    public SQLitePromotion getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord");
    }
}
