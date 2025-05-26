package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

record SQLitePaidTicket(int id) implements SQLiteTable<SQLitePaidTicket> {

    static final String TABLE_NAME = "PaidTickets";
    static private final int COLUMNS_NUMBER = 1;

    static private final String COLUMNS =
            "id INTEGER NOT NULL," +
            "PRIMARY KEY (id)," +
            "FOREIGN KEY (id) REFERENCES Tickets(id) ON DELETE CASCADE";

    static final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            SQLiteTable.getAllQuery(TABLE_NAME);

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s
            WHERE id=?;
            """,
            TABLE_NAME
    );

    static private final String GET_QUERY = String.format("""
            %s WHERE id=?;
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
        st.setInt(COLUMNS_NUMBER, id);
        st.executeUpdate();
    }

    public void insertRecordIfNotExists(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(String.format("""
                INSERT INTO %s (id)
                SELECT (?)
                WHERE NOT EXISTS (SELECT * FROM %s WHERE id=?);
                """,
                TABLE_NAME,
                TABLE_NAME
        ));
        st.setInt(1,id);
        st.setInt(2,id);
        st.executeUpdate();
    }

    @Override
    public SQLitePaidTicket getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return toRecord(rs);
    }

    @Override
    public SQLitePaidTicket toRecord(ResultSet rs) throws SQLException {
        return new SQLitePaidTicket(rs.getInt("id"));
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setInt(1,id);
        st.executeUpdate();
    }

}
