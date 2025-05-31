package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

record SQLiteFidelityUser(String userEmail) implements SQLiteTable<SQLiteFidelityUser> {

    static final String TABLE_NAME = "FidelityUsers";
    static final int COLUMNS_NUMBER = 1;

    static private final String COLUMNS =
            "userEmail TEXT NOT NULL, " +
            "PRIMARY KEY (userEmail)," +
            "FOREIGN KEY (userEmail) REFERENCES Users(email) ON DELETE CASCADE";

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
    public SQLiteFidelityUser getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setString(1, userEmail);
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return toRecord(rs);
    }

    @Override
    public SQLiteFidelityUser toRecord(ResultSet rs) throws SQLException{
        return new SQLiteFidelityUser(rs.getString("userEmail"));
    }
}
