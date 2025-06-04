package it.trenical.client.connection;

import io.grpc.*;
import it.trenical.client.config.Config;
import it.trenical.client.config.ConfigManager;

public class GrpcConnection {

    // Singleton class
    private static GrpcConnection instance;

    private final ManagedChannel channel;

    private GrpcConnection() {
        Config config = ConfigManager.INSTANCE.config;
        channel = ManagedChannelBuilder.forAddress(config.server.address, config.server.port).usePlaintext().build();
    }

    public static synchronized GrpcConnection getInstance() {
        if (instance == null) instance = new GrpcConnection();
        return instance;
    }

    public static synchronized Channel getChannel() {
        if (instance == null) instance = new GrpcConnection();
        return instance.channel;
    }
}
