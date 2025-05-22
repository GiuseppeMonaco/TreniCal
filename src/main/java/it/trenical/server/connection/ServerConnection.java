package it.trenical.server.connection;

import java.io.IOException;

public interface ServerConnection {

    /**
     * Start the server.
     * @throws IOException if IO exception
     */
    void start() throws IOException;

    /**
     * Wait for the server shutdown.
     * @throws InterruptedException if is interurpted
     */
    void blockUntilShutdown() throws InterruptedException;

}
