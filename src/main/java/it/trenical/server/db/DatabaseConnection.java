package it.trenical.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseConnection {

    Connection getConnection();
    ResultSet executeQuery(String select, String from, String where) throws SQLException;

}
