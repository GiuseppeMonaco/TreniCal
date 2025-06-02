package it.trenical.client.notification;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.GrpcConnection;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.Login;
import it.trenical.common.Promotion;
import it.trenical.common.SessionToken;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;
import it.trenical.grpc.*;
import it.trenical.grpcUtil.GrpcConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.trenical.grpcUtil.GrpcConverter.convert;

public class NotificationHandler implements Login.Observer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);

    private final NotificationServiceGrpc.NotificationServiceStub asyncStub;
    private final NotificationServiceGrpc.NotificationServiceBlockingStub blockingStub;

    public NotificationHandler() {
        asyncStub = NotificationServiceGrpc.newStub(GrpcConnection.getChannel());
        blockingStub = NotificationServiceGrpc.newBlockingStub(GrpcConnection.getChannel());
    }

    @Override
    public void updateOnLogin() {
        subscribeUserToStreams(Client.getInstance().getCurrentToken());
    }

    public boolean isUserSubscribedToFidelityPromotions(SessionToken token) throws UnreachableServer, InvalidSessionTokenException {

        Subscribe request = Subscribe.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .build();

        try {
            FidelityPromotionsStatusReply reply = blockingStub.fidelityPromotionsStatus(request);
            if(!reply.getWasTokenValid()) throw new InvalidSessionTokenException("Given token is invalid");
            return reply.getIsSubscribed();
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying fidelityPromotionsStatus");
            throw new UnreachableServer("Unreachable server");
        }
    }

    public boolean fidelityPromotionsSubscribe(SessionToken token) throws UnreachableServer, InvalidSessionTokenException {

         Subscribe request = Subscribe.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .build();

        try {
            SubscribeReply reply = blockingStub.fidelityPromotionsSubscribe(request);
            if(!reply.getWasTokenValid()) throw new InvalidSessionTokenException("Given token is invalid");
            return reply.getIsDone();
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying fidelityPromotionsSubscribe");
            throw new UnreachableServer("Unreachable server");
        }
    }

    public boolean fidelityPromotionsUnubscribe(SessionToken token) throws UnreachableServer, InvalidSessionTokenException {

        Subscribe request = Subscribe.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .build();

        try {
            SubscribeReply reply = blockingStub.fidelityPromotionsUnsubscribe(request);
            if(!reply.getWasTokenValid()) throw new InvalidSessionTokenException("Given token is invalid");
            return reply.getIsDone();
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying fidelityPromotionsUnubscribe");
            throw new UnreachableServer("Unreachable server");
        }
    }

    private void subscribeUserToStreams(SessionToken token) {
        subscribeAlmostExpiredBooking(token);
        subscribeExpiredBooking(token);
        subscribeTripsDelete(token);
        subscribeFidelityPromotions(token);
    }

    /**
     * Subscribes to the almostExpiredBooking stream.
     * The server will send TicketStream objects whenever a booking is about to expire.
     */
    private void subscribeAlmostExpiredBooking(SessionToken token) {
        Client client = Client.getInstance();

        Subscribe request = Subscribe.newBuilder()
                .setToken(convert(token))
                .build();

        asyncStub.almostExpiredBooking(request, new StreamObserver<>() {
            @Override
            public void onNext(TicketStream ticketStream) {
                if (!ticketStream.getWasTokenValid()) {
                    logger.error("[NOTIFICATION] almostExpiredBooking error: token not valid. Stream closed.");
                    return;
                }

                Ticket ticket = convert(ticketStream.getTicket());
                Notification notification = new AlmostExpiredBookingNotification(ticketStream.getTimestamp(),ticket);
                client.addNotification(notification);
                logger.info("[NOTIFICATION] One of your booked tickets is about to expire: {}", ticket);
            }

            @Override
            public void onError(Throwable t) {
                logger.error("[NOTIFICATION] almostExpiredBooking errore: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("[NOTIFICATION] subscribeAlmostExpiredBooking stream closed by server.");
            }
        });
    }

    /**
     * Subscribes to the expiredBooking stream.
     * The server will send a TicketStream whenever a booking has expired.
     */
    private void subscribeExpiredBooking(SessionToken token) { // TODO mettere il controllo se il trip non è stato cancellato perché è partito
        Client client = Client.getInstance();

        Subscribe request = Subscribe.newBuilder()
                .setToken(convert(token))
                .build();

        asyncStub.expiredBooking(request, new StreamObserver<>() {
            @Override
            public void onNext(TicketStream ticketStream) {
                if (!ticketStream.getWasTokenValid()) {
                    logger.error("[NOTIFICATION] expiredBooking error: token not valid. Stream closed.");
                    return;
                }

                try {
                    client.queryTickets();
                } catch (UnreachableServer | InvalidSessionTokenException ignored) {}

                Ticket ticket = convert(ticketStream.getTicket());
                Notification notification = new ExpiredBookingNotification(ticketStream.getTimestamp(), ticket);
                client.addNotification(notification);
                logger.info("[NOTIFICATION] One of your booked tickets has just expired: {}", ticket);
            }

            @Override
            public void onError(Throwable t) {
                logger.error("[NOTIFICATION] expiredBooking error: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("[NOTIFICATION] subscribeExpiredBooking stream closed by server.");
            }
        });
    }

    /**
     * It subscribes to the tripsDelete stream.
     * The server will send a TripStream each time a trip is deleted.
     */
    private void subscribeTripsDelete(SessionToken token) {
        Client client = Client.getInstance();

        Subscribe request = Subscribe.newBuilder()
                .setToken(convert(token))
                .build();

        asyncStub.tripsDelete(request, new StreamObserver<>() {
            @Override
            public void onNext(TripStream tripStream) {
                if (!tripStream.getWasTokenValid()) {
                    logger.error("[NOTIFICATION] tripsDelete error: token not valid. Stream closed.");
                    return;
                }

                try {
                    client.queryTickets();
                } catch (UnreachableServer | InvalidSessionTokenException ignored) {}

                Trip trip = convert(tripStream.getTrip());
                Notification notification = new TripCancelledNotification(tripStream.getTimestamp(),trip);
                client.addNotification(notification);
                logger.info("[NOTIFICATION] A trip has been cancelled: {}", trip);
            }

            @Override
            public void onError(Throwable t) {
                logger.error("[NOTIFICATION] tripsDelete error: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("[NOTIFICATION] tripsDelete stream closed by server.");
            }
        });
    }

    /**
     * Subscribes to the fidelityPromotions stream.
     * The server will send a PromotionStream every time a promotion dedicated to fidelity users arrives.
     */
    private void subscribeFidelityPromotions(SessionToken token) {
        Client client = Client.getInstance();

        Subscribe request = Subscribe.newBuilder()
                .setToken(convert(token))
                .build();

        asyncStub.fidelityPromotions(request, new StreamObserver<>() {
            @Override
            public void onNext(PromotionStream promoStream) {
                if (!promoStream.getWasTokenValid()) {
                    logger.error("[NOTIFICATION] fidelityPromotions error: token not valid. Stream closed.");
                    return;
                }

                Promotion promotion = convert(promoStream.getPromotion());
                Notification notification = new FidelityPromotionNotification(promoStream.getTimestamp(),promotion);
                client.addNotification(notification);
                logger.info("[NOTIFICATION] New Fidelity promotion: {}", promotion);
            }

            @Override
            public void onError(Throwable t) {
                logger.error("[NOTIFICATION] fidelityPromotions error: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                logger.info("[NOTIFICATION] fidelityPromotions stream closed by server.");
            }
        });
    }
}
