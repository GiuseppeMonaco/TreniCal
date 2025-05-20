package it.trenical.server.db.SQLite;

import it.trenical.common.TrainType;
import it.trenical.common.TrainTypeData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTrainType implements SQLiteTable<TrainType>, TrainType {

    static private final String TABLE_NAME = "TrainTypes";
    static private final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "name TEXT(100) NOT NULL," +
            "price REAL NOT NULL," +
            "PRIMARY KEY (name)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    private final TrainType data;

    public SQLiteTrainType(TrainType data) {
        this.data = data;
    }

    public SQLiteTrainType(String name, float price) {
        this(new TrainTypeData(name, price));
    }

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1,getName());
        st.setFloat(COLUMNS_NUMBER,getPrice());
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("updateRecord"); // TODO
    }

    @Override
    public SQLiteTrainType getRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getRecord"); // TODO
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public float getPrice() {
        return data.getPrice();
    }
}
