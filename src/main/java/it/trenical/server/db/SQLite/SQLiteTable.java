package it.trenical.server.db.SQLite;

import it.trenical.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

interface SQLiteTable<T> {

    static void initTable(Statement statement, String tableName, String columns) throws SQLException {
        statement.execute(String.format("CREATE TABLE IF NOT EXISTS %s (%s);", tableName, columns));
    }

    static String getInsertQuery(String tableName, int columnsNumber) {
        String questionMarks = "?,".repeat(columnsNumber).substring(0,columnsNumber*2-1);
        return String.format("INSERT INTO %s VALUES (%s);", tableName, questionMarks);
    }

    static String getAllQuery(String tableName) {
        return String.format("SELECT * FROM %s", tableName);
    }

    String getTableName();

    int getColumnsNumber();

    void insertRecord(DatabaseConnection db) throws SQLException;

    T getRecord(DatabaseConnection db) throws SQLException;

    T toRecord(ResultSet rs) throws SQLException;

    default String getInsertQuery() {
        String questionMarks = "?,".repeat(getColumnsNumber()).substring(0,getColumnsNumber()*2-1);
        return String.format("INSERT INTO %s VALUES (%s);", getTableName(), questionMarks);
    }

    String getAllQuery();

    default Collection<T> getAllRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(getAllQuery());

        LinkedList<T> ret = new LinkedList<>();
        while(rs.next()) {
            ret.add(toRecord(rs));
        }
        return ret;
    }

    default void updateRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("updateRecord");
    }

    default void deleteRecord(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("deleteRecord");
    }

    default Collection<T> getSimilarRecords(DatabaseConnection db) throws SQLException {
        throw new UnsupportedOperationException("getSimilarRecords");
    }
}
