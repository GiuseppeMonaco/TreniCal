package it.trenical.server.connection;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import it.trenical.server.auth.GrpcAuthImpl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcServerConnection implements ServerConnection {

    // Singleton class
    private static GrpcServerConnection instance;

    private final Logger logger = Logger.getLogger(GrpcServerConnection.class.getName());

    private static final int PORT = 8008;
    private static final int THREAD_NUMBER = 4;

    private Server server;

    private GrpcServerConnection() {}

    public static synchronized GrpcServerConnection getInstance() {
        if (instance == null) instance = new GrpcServerConnection();
        return instance;
    }

    @Override
    public void start() throws IOException {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
                .executor(executor)
                .addService(new GrpcAuthImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            try {
                this.stop();
            } catch (InterruptedException e) {
                if (server != null) {
                    server.shutdownNow();
                }
                e.printStackTrace(System.err);
            } finally {
                executor.shutdown();
            }
            System.err.println("Server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    @Override
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
