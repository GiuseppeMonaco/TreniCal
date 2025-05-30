package it.trenical.server.db.exceptions;

public class ForeignKeyException extends Exception {
    public ForeignKeyException(String message) {
        super(message);
    }
}
