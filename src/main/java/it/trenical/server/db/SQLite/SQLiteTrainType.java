package it.trenical.server.db.SQLite;

import it.trenical.common.TrainTypeData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTrainType extends TrainTypeData implements SQLiteTable {

    static private final String TABLE_NAME = "TrainTypes";
    static private final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "name TEXT(100) NOT NULL," +
            "price REAL NOT NULL," +
            "PRIMARY KEY (name)";

    static private final String INSERT_QUERY =
            SQLiteTable.buildInsertQuery(TABLE_NAME,COLUMNS_NUMBER);

    public SQLiteTrainType(String name, float price) {
        super(name, price);
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
        // TODO
    }
}
