package it.trenical.server.db.SQLite;

import it.trenical.common.Ticket;
import it.trenical.common.User;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;

public record SQLiteAlmostExpiredBookingNotification(User user, Ticket book, long timestamp) implements SQLiteTable<SQLiteAlmostExpiredBookingNotification> {

    static final String TABLE_NAME = "AlmostExpiredBookingNotifications";
    static final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS = """
            ticket INTEGER NOT NULL,
            timestamp INTEGER NOT NULL,
            PRIMARY KEY (ticket),
            FOREIGN KEY (ticket) REFERENCES Tickets(id) ON DELETE CASCADE
            """;

    static final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            new SQLiteTicket(null).getAllQuery().replace("SELECT",
                    "SELECT timestamp,"
            );

    static final String DELETE_QUERY = String.format("""
            DELETE FROM %s t
            WHERE EXISTS (
                SELECT 1
                FROM %s tt
                WHERE t.ticket = tt.id
                AND tt.userEmail=?
            );
            """,
            TABLE_NAME,
            new SQLiteTicket(null).getTableName()
    );

    static private final String GET_QUERY = String.format("""
            %s AND tk.userEmail=?;
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
        st.setInt(1,book.getId());
        st.setLong(2,timestamp);
        st.executeUpdate();
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1,user.getEmail());
        st.executeUpdate();
    }

    public Collection<SQLiteAlmostExpiredBookingNotification> getSimilarRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setString(1,user.getEmail());
        ResultSet rs = st.executeQuery();

        Collection<SQLiteAlmostExpiredBookingNotification> ret = new LinkedList<>();
        while (rs.next()) ret.add(toRecord(rs));
        return ret;
    }

    @Override
    public SQLiteAlmostExpiredBookingNotification getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord not supported");
    }

    @Override
    public SQLiteAlmostExpiredBookingNotification toRecord(ResultSet rs) throws SQLException {
        Ticket t = new SQLiteTicket(null).toRecord(rs);
        return new SQLiteAlmostExpiredBookingNotification(t.getUser(), t, rs.getLong("timestamp"));
    }
}
