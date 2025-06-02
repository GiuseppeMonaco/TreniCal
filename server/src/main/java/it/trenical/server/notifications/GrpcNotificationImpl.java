package it.trenical.server.notifications;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import it.trenical.common.User;
import it.trenical.grpc.*;
import it.trenical.grpcUtil.GrpcConverter;
import it.trenical.server.Server;
import it.trenical.server.auth.BiMapTokenManager;
import it.trenical.server.auth.TokenManager;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.SQLiteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class GrpcNotificationImpl extends NotificationServiceGrpc.NotificationServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(GrpcNotificationImpl.class);

    private final DatabaseConnection db = Server.INSTANCE.getDatabase();

    private final TokenManager tokenManager = BiMapTokenManager.INSTANCE;

    private final NotificationManager notificationManager = NotificationManager.INSTANCE;

    @Override
    public void almostExpiredBooking(Subscribe request, StreamObserver<TicketStream> responseObserver) {

        TicketStream.Builder b = TicketStream.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        notificationManager.addAlmostExpiredBookingUser(user, responseObserver);

        // To do on connection cancel
        if (responseObserver instanceof ServerCallStreamObserver) {
            ((ServerCallStreamObserver<?>) responseObserver).setOnCancelHandler(() -> notificationManager.removeAlmostExpiredBookingUser(user));
        }
    }

    @Override
    public void expiredBooking(Subscribe request, StreamObserver<TicketStream> responseObserver) {

        TicketStream.Builder b = TicketStream.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        notificationManager.addExpiredBookingUser(user, responseObserver);

        // To do on connection cancel
        if (responseObserver instanceof ServerCallStreamObserver) {
            ((ServerCallStreamObserver<?>) responseObserver).setOnCancelHandler(() -> notificationManager.removeExpiredBookingUser(user));
        }
    }

    @Override
    public void tripsDelete(Subscribe request, StreamObserver<TripStream> responseObserver) {

        TripStream.Builder b = TripStream.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        notificationManager.addTripsDeleteUser(user, responseObserver);

        // To do on connection cancel
        if (responseObserver instanceof ServerCallStreamObserver) {
            ((ServerCallStreamObserver<?>) responseObserver).setOnCancelHandler(() -> notificationManager.removeTripsDeleteUser(user));
        }
    }

    @Override
    public void fidelityPromotions(Subscribe request, StreamObserver<PromotionStream> responseObserver) {

        PromotionStream.Builder b = PromotionStream.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        notificationManager.addFidelityPromotionsUser(user,responseObserver);

        // To do on connection cancel
        if (responseObserver instanceof ServerCallStreamObserver) {
            ((ServerCallStreamObserver<?>) responseObserver).setOnCancelHandler(() -> notificationManager.removeFidelityPromotionsUser(user));
        }
    }

    @Override
    public void fidelityPromotionsStatus(Subscribe request, StreamObserver<FidelityPromotionsStatusReply> responseObserver) {

        FidelityPromotionsStatusReply.Builder b = FidelityPromotionsStatusReply.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        b.setIsSubscribed(notificationManager.isFidelityUserSubscribed(user));
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override
    public void fidelityPromotionsSubscribe(Subscribe request, StreamObserver<SubscribeReply> responseObserver) {

        SubscribeReply.Builder b = SubscribeReply.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        try {
            user = new SQLiteUser(user).getRecord(db);
        } catch (SQLException e) {
            logger.warn("Cannot verify if user is subscribed to fidelity program: {}",e.getMessage());
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }

        if(!user.isFidelity()) {
            logger.warn("Not Fidelity user cannot subscribe to Fidelity program notifications");
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }

        b.setIsDone(notificationManager.subscribeFidelityUser(user));
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override
    public void fidelityPromotionsUnsubscribe(Subscribe request, StreamObserver<SubscribeReply> responseObserver) {

        SubscribeReply.Builder b = SubscribeReply.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        b.setIsDone(notificationManager.unsubscribeFidelityUser(user));
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }
}
