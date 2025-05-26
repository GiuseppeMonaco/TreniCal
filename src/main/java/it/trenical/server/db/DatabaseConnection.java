package it.trenical.server.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {

    Connection getConnection();
    void close();

    @FunctionalInterface
    interface SQLConsumer {
        void accept() throws SQLException;
    }
    void atomicTransaction(SQLConsumer query) throws SQLException;
}
