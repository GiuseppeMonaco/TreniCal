package it.trenical.server.db.SQLite;

import it.trenical.common.User;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public record SQLiteNotifiableFidelityUser(String userEmail) implements SQLiteTable<SQLiteNotifiableFidelityUser> {

    static final String TABLE_NAME = "NotifialbleFidelityUsers";
    static final int COLUMNS_NUMBER = 1;

    static private final String COLUMNS =
            "userEmail TEXT NOT NULL, " +
            "PRIMARY KEY (userEmail)," +
            "FOREIGN KEY (userEmail) REFERENCES FidelityUsers(userEmail) ON DELETE CASCADE";

    static final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            SQLiteTable.getAllQuery(TABLE_NAME);

    static final String DELETE_QUERY = String.format("""
            DELETE FROM %s
            WHERE userEmail=?;
            """,
            TABLE_NAME
    );

    static private final String GET_QUERY = String.format("""
            %s WHERE userEmail=?;
            """,
            ALL_QUERY
    );

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    public SQLiteNotifiableFidelityUser(User user) {
        this(user.getEmail());
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

    public void insertRecordIfNotExists(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(String.format("""
                INSERT INTO %s (userEmail)
                SELECT (?)
                WHERE NOT EXISTS (SELECT * FROM %s WHERE userEmail=?);
                """,
                TABLE_NAME,
                TABLE_NAME
        ));
        st.setString(1,userEmail);
        st.setString(2,userEmail);
        st.executeUpdate();
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1,userEmail);
        st.executeUpdate();
    }

    @Override
    public SQLiteNotifiableFidelityUser getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setString(1, userEmail);
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return toRecord(rs);
    }

    @Override
    public SQLiteNotifiableFidelityUser toRecord(ResultSet rs) throws SQLException{
        return new SQLiteNotifiableFidelityUser(rs.getString("userEmail"));
    }
}
