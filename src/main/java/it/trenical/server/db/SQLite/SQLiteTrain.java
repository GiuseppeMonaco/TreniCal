package it.trenical.server.db.SQLite;

import it.trenical.common.Train;
import it.trenical.common.TrainType;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTrain implements SQLiteTable<Train>, Train {

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

    private final Train data;

    public SQLiteTrain(Train data) {
        this.data = data;
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
        throw new UnsupportedOperationException("updateRecord"); // TODO
    }

    @Override
    public SQLiteTrain getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord"); // TODO
    }

    @Override
    public int getId() {
        return data.getId();
    }

    @Override
    public TrainType getType() {
        return data.getType();
    }

    @Override
    public int getEconomyCapacity() {
        return data.getEconomyCapacity();
    }

    @Override
    public int getBusinessCapacity() {
        return data.getBusinessCapacity();
    }
}
