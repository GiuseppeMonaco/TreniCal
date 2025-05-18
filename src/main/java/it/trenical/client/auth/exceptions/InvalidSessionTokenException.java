package it.trenical.client.auth.exceptions;

public class InvalidSessionTokenException extends Exception {
    public InvalidSessionTokenException(String message) {
        super(message);
    }
}
