package it.trenical.server.db.SQLite;

import it.trenical.common.Train;
import it.trenical.common.TrainData;
import it.trenical.common.TrainType;
import it.trenical.common.TrainTypeData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLiteTrain implements SQLiteTable<Train>, Train {

    static final String TABLE_NAME = "Trains";
    static private final int COLUMNS_NUMBER = 4;

    static private final String COLUMNS =
            "id INTEGER NOT NULL," +
            "type TEXT NOT NULL," +
            "economyCapacity INTEGER NOT NULL," +
            "businessCapacity INTEGER NOT NULL," +
            "PRIMARY KEY (id)," +
            "FOREIGN KEY (type) REFERENCES TrainTypes(name) ON DELETE CASCADE";

    static private final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            String.format("SELECT * FROM %s t, %s tt WHERE T.type=TT.name",
                    TABLE_NAME,
                    SQLiteTrainType.TABLE_NAME
            );

    static private final String GET_QUERY = ALL_QUERY + " AND id=?;";

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s
            WHERE id=?;
            """,
            TABLE_NAME
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
    private final Train data;

    public SQLiteTrain(Train data) {
        this.data = data;
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
    public SQLiteTrain getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setInt(1, getId());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return new SQLiteTrain(toRecord(rs));
    }

    @Override
    public Train toRecord(ResultSet rs) throws SQLException {
        return TrainData.newBuilder(rs.getInt("id"))
                .setType(new TrainTypeData(rs.getString("name"),rs.getFloat("price")))
                .setEconomyCapacity(rs.getInt("economyCapacity"))
                .setBusinessCapacity(rs.getInt("businessCapacity"))
                .build();
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setInt(1, getId());
        st.executeUpdate();
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
