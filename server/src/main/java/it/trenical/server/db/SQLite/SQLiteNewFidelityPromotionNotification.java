package it.trenical.server.db.SQLite;

import it.trenical.common.Promotion;
import it.trenical.common.User;
import it.trenical.common.UserData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;

public record SQLiteNewFidelityPromotionNotification(User user, Promotion promotion, long timestamp) implements SQLiteTable<SQLiteNewFidelityPromotionNotification> {

    static final String TABLE_NAME = "NewFidelityPromotionsNotifications";
    static private final int COLUMNS_NUMBER = 4;

    static private final String COLUMNS = """
            id INTEGER NOT NULL,
            userEmail TEXT NOT NULL,
            promotion TEXT NOT NULL,
            timestamp INTEGER NOT NULL,
            PRIMARY KEY (id),
            FOREIGN KEY (userEmail) REFERENCES Users(email) ON DELETE CASCADE,
            FOREIGN KEY (promotion) REFERENCES Promotions(code) ON DELETE CASCADE
            """;

    static private final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY = String.format("""
            SELECT *
            FROM %s p, %s pp
            WHERE p.promotion = pp.code
            """,
            TABLE_NAME,
            SQLitePromotion.TABLE_NAME
    );

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s
            WHERE userEmail=?;
            """,
            TABLE_NAME
    );

    static private final String GET_QUERY = String.format("""
            %s AND userEmail=?;
            """,
            ALL_QUERY
    );

    public static void initTable(Statement statement) throws SQLException {
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
        st.setString(2, user.getEmail());
        st.setString(3, promotion.getCode());
        st.setLong(4,timestamp);
        st.executeUpdate();
    }

    @Override
    public SQLiteNewFidelityPromotionNotification getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord not supported");
    }

    public Collection<SQLiteNewFidelityPromotionNotification> getSimilarRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setString(1,user.getEmail());
        ResultSet rs = st.executeQuery();

        Collection<SQLiteNewFidelityPromotionNotification> ret = new LinkedList<>();
        while (rs.next()) ret.add(toRecord(rs));
        return ret;
    }

    @Override
    public SQLiteNewFidelityPromotionNotification toRecord(ResultSet rs) throws SQLException {
        Promotion p = new SQLitePromotion(null).toRecord(rs);
        return new SQLiteNewFidelityPromotionNotification(new UserData(rs.getString("userEmail")), p, rs.getLong("timestamp"));
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1,user.getEmail());
        st.executeUpdate();
    }
}
