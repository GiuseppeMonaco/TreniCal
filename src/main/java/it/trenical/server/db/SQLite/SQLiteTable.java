package it.trenical.server.db.SQLite;

import it.trenical.common.Data;
import it.trenical.server.db.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Statement;

interface SQLiteTable {

    static void initTable(Statement statement, String tableName, String columns) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");");
    }

    static String buildInsertQuery(String tableName, int columnsNumber) {
        String questionMarks = "?,".repeat(columnsNumber).substring(0,columnsNumber*2-1);
        return "INSERT INTO " + tableName + " VALUES (" + questionMarks + ");";
    }

    void insertRecord(DatabaseConnection db) throws SQLException;
    void updateRecord(DatabaseConnection db) throws SQLException;
    Data getRecord(DatabaseConnection db) throws SQLException;
}
