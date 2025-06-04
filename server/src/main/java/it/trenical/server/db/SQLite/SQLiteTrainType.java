package it.trenical.server.db.SQLite;

import it.trenical.common.TrainType;
import it.trenical.common.TrainTypeData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLiteTrainType implements SQLiteTable<TrainType>, TrainType {

    static final String TABLE_NAME = "TrainTypes";
    static final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "name TEXT(100) NOT NULL," +
            "price REAL NOT NULL," +
            "PRIMARY KEY (name)";

    static private final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY =
            SQLiteTable.getAllQuery(TABLE_NAME);

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s
            WHERE name=?;
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

    private final TrainType data;

    public SQLiteTrainType(TrainType data) {
        this.data = data;
    }

    public SQLiteTrainType(String name, float price) {
        this(new TrainTypeData(name, price));
    }

    public SQLiteTrainType(String name) {
        this(new TrainTypeData(name));
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
    public SQLiteTrainType getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE name=?;");
        st.setString(1, getName());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return new SQLiteTrainType(toRecord(rs));
    }

    @Override
    public TrainType toRecord(ResultSet rs) throws SQLException {
        return new TrainTypeData(
                rs.getString("name"),
                rs.getFloat("price")
        );
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1, getName());
        st.executeUpdate();
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
