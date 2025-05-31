package it.trenical.client.connection;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class GrpcConnection {

    // Singleton class
    private static GrpcConnection instance;

    private static final String SERVERIP = "localhost";
    private static final int SERVERPORT = 8008;

    private final ManagedChannel channel;

    private GrpcConnection() {
        channel = Grpc.newChannelBuilder(
                SERVERIP + ":" + SERVERPORT,
                InsecureChannelCredentials.create()
        ).build();
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
