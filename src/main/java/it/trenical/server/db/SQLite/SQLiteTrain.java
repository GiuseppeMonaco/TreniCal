package it.trenical.server.db.SQLite;

import it.trenical.common.TrainData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTrain extends TrainData implements SQLiteTable {

    static private final String TABLE_NAME = "Trains";
    static private final int COLUMNS_NUMBER = 4;

    static private final String COLUMNS =
            "id INTEGER NOT NULL," +
            "type TEXT NOT NULL," +
            "economyCapacity INTEGER NOT NULL," +
            "businessCapacity INTEGER NOT NULL," +
            "PRIMARY KEY (id)," +
            "FOREIGN KEY (type) REFERENCES TrainTypes(name)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    public static Builder newBuilder(int id) {
        return new Builder(id);
    }

    private SQLiteTrain(Builder builder) {
        super(builder);
    }

    public static class Builder extends TrainData.Builder {
        private Builder(int id) {
            super(id);
        }

        @Override
        public SQLiteTrain build() {
            return (SQLiteTrain) super.build();
        }
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setInt(1,getId());
        st.setString(2,getType().getName());
        st.setInt(3,getEconomyCapacity());
        st.setInt(COLUMNS_NUMBER,getBusinessCapacity());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
