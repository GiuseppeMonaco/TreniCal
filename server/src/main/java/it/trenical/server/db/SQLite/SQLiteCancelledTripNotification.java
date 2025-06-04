package it.trenical.server.db.SQLite;

import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.common.UserData;
import it.trenical.server.db.DatabaseConnection;

import java.io.*;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;

public record SQLiteCancelledTripNotification(User user, Trip trip, long timestamp) implements SQLiteTable<SQLiteCancelledTripNotification> {

    static final String TABLE_NAME = "CancelledTripNotifications";
    static final int COLUMNS_NUMBER = 4;

    static private final String COLUMNS = """
            id INTEGER NOT NULL,
            userEmail TEXT NOT NULL,
            trip BLOB NOT NULL,
            timestamp INTEGER NOT NULL,
            PRIMARY KEY (id),
            FOREIGN KEY (userEmail) REFERENCES Users(email) ON DELETE CASCADE
            """;

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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(trip);
        } catch (IOException e) {
            throw new SQLException(e);
        }

        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(2,user.getEmail());
        st.setBytes(3,baos.toByteArray());
        st.setLong(4,timestamp);
        st.executeUpdate();
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1,user.getEmail());
        st.executeUpdate();
    }

    public Collection<SQLiteCancelledTripNotification> getSimilarRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setString(1,user.getEmail());
        ResultSet rs = st.executeQuery();

        Collection<SQLiteCancelledTripNotification> ret = new LinkedList<>();
        while (rs.next()) ret.add(toRecord(rs));
        return ret;
    }

    @Override
    public SQLiteCancelledTripNotification getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord not supported");
    }

    @Override
    public SQLiteCancelledTripNotification toRecord(ResultSet rs) throws SQLException {
        ByteArrayInputStream bis = new ByteArrayInputStream(rs.getBytes("trip"));
        Trip t;
        try {
            ObjectInputStream in = new ObjectInputStream(bis);
            t = (Trip) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SQLException(e);
        }
        return new SQLiteCancelledTripNotification(new UserData(rs.getString("userEmail")), t, rs.getLong("timestamp"));
    }
}
