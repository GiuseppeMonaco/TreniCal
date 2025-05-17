package it.trenical.server.db;

import it.trenical.common.TrainType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public record SQLiteTrainType(String name, float price) implements TrainType, SQLiteTable {

    static private final String TABLE_NAME = "TrainTypes";
    static private final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "name TEXT(100) NOT NULL," +
            "price REAL NOT NULL," +
            "PRIMARY KEY (name)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setString(1,name);
        st.setFloat(COLUMNS_NUMBER,price);
        st.executeUpdate();
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        // TODO
    }
}
