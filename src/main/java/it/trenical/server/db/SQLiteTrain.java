package it.trenical.server.db;

import it.trenical.common.Train;
import it.trenical.common.TrainType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLiteTrain(int id, TrainType type, int economyCapacity, int businessCapacity) implements Train, SQLiteTable {

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

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public TrainType getType() {
        return type;
    }

    @Override
    public int getEconomyCapacity() {
        return economyCapacity;
    }

    @Override
    public int getBusinessCapacity() {
        return businessCapacity;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setInt(1,id);
        st.setString(2,type.getName());
        st.setInt(3,economyCapacity);
        st.setInt(COLUMNS_NUMBER,businessCapacity);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
