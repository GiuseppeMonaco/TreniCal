package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLitePaidTicket(String userEmail, int id) implements SQLiteTable {

    static private final String TABLE_NAME = "PaidTickets";
    static private final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "id INTEGER NOT NULL," +
            "userEmail TEXT NOT NULL," +
            "PRIMARY KEY (id,userEmail)," +
            "FOREIGN KEY (id,userEmail) REFERENCES Tickets(id,userEmail)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1, userEmail);
        st.setInt(COLUMNS_NUMBER, id);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
