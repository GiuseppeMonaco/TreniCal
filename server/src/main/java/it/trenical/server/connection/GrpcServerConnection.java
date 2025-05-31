package it.trenical.server.connection;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import it.trenical.server.auth.GrpcAuthImpl;
import it.trenical.server.query.GrpcQueryImpl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import it.trenical.server.request.GrpcRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcServerConnection implements ServerConnection {

    // Singleton class
    private static GrpcServerConnection instance;

    private final Logger logger = LoggerFactory.getLogger(GrpcServerConnection.class);

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
                .addService(new GrpcQueryImpl())
                .addService(new GrpcRequestImpl())
                .build()
                .start();
        logger.info("gRPC server started, listening on port {}", PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            try {
                this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace(System.err);
            } finally {
                executor.shutdown();
            }
            server.shutdownNow();
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
        logger.info("gRPC server stopped");
    }

}
