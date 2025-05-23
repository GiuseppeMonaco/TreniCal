package it.trenical.server.db;

import java.sql.Connection;

public interface DatabaseConnection {

    Connection getConnection();
    void close();

}
