package it.trenical.server.connection;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import it.trenical.server.auth.GrpcAuthImpl;
import it.trenical.server.config.Config;
import it.trenical.server.config.ConfigManager;
import it.trenical.server.notifications.GrpcNotificationImpl;
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

    private Server server;

    private GrpcServerConnection() {}

    public static synchronized GrpcServerConnection getInstance() {
        if (instance == null) instance = new GrpcServerConnection();
        return instance;
    }

    @Override
    public void start() throws IOException {

        Config config = ConfigManager.INSTANCE.config;
        final int port = config.server.port;

        ExecutorService executor = Executors.newFixedThreadPool(config.server.threadNumber);
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .executor(executor)
                .addService(new GrpcAuthImpl())
                .addService(new GrpcQueryImpl())
                .addService(new GrpcRequestImpl())
                .addService(new GrpcNotificationImpl())
                .build()
                .start();
        logger.info("gRPC server started, listening on port {}", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            try {
                this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error(e.getMessage());
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
