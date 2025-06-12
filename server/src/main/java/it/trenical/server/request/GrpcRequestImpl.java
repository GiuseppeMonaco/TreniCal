package it.trenical.server.request;

import io.grpc.stub.StreamObserver;
import it.trenical.common.*;
import it.trenical.common.Promotion;
import it.trenical.common.Ticket;
import it.trenical.common.User;
import it.trenical.grpc.*;
import it.trenical.grpcUtil.GrpcConverter;
import it.trenical.server.Server;
import it.trenical.server.auth.BiMapTokenManager;
import it.trenical.server.auth.TokenManager;
import it.trenical.server.config.Config;
import it.trenical.server.config.ConfigManager;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.SQLitePromotion;
import it.trenical.server.db.SQLite.SQLiteTicket;
import it.trenical.server.db.SQLite.SQLiteTrip;
import it.trenical.server.db.SQLite.SQLiteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcRequestImpl extends RequestServiceGrpc.RequestServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(GrpcRequestImpl.class);

    private final Config config = ConfigManager.INSTANCE.config;
    private final float priceDistanceMultiplier = config.logic.price.distanceMultiplier;
    private final float priceBusinessMultiplier = config.logic.price.businessMultiplier;

    private final DatabaseConnection db = Server.INSTANCE.getDatabase();

    private final TokenManager tokenManager = BiMapTokenManager.INSTANCE;

    private final Server server = Server.INSTANCE;

    private static final int SUCCESS_CODE = 0;
    private static final int INVALID_TOKEN_ERROR_CODE = 1;
    private static final int GENERIC_ERROR_CODE = 2;
    private static final int INVALID_TICKET_ERROR_CODE = 3;
    private static final int NO_CHANGE_ERROR_CODE = 4;
    private static final int NOT_AVAILABLE_SEATS_ERROR_CODE = 5;
    private static final int CANCELLED_TRIP = 6;
    private static final int CANCELLED_PROMOTION = 7;

    @Override
    public void buyTickets(BuyTicketsRequest request, StreamObserver<BuyTicketsReply> responseObserver) {

        BuyTicketsReply.Builder b = BuyTicketsReply.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            logger.debug("Invalid token request on buyTickets request. Token -> {}",request.getToken().getToken());
            b.setErrorCode(INVALID_TOKEN_ERROR_CODE);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }

        Collection<SQLiteTicket> tickets = request.getTicketsList().stream()
                .map(GrpcConverter::convert)
                .map((t) -> toSanitizedTicket(t,user,true))
                .map(SQLiteTicket::new)
                .toList();

        AtomicInteger errorCode = new AtomicInteger(GENERIC_ERROR_CODE);
        try {
            db.atomicTransaction(() -> {
                Collection<Promotion> currentUsedPromo = new LinkedList<>();
                for(SQLiteTicket t : tickets) {

                    Promotion p = t.getPromotion();

                    // Check if the promotion still exists
                    if (p != null && new SQLitePromotion(p).getRecord(db) == null) {
                        errorCode.set(CANCELLED_PROMOTION);
                        throw new SQLException(String.format("The promotion %s is not longer valid", p.getName()));
                    }

                    SQLiteTrip tr = new SQLiteTrip(t.getTrip());

                    // Check if the trip still exists
                    if (tr.getRecord(db) == null) {
                        errorCode.set(CANCELLED_TRIP);
                        throw new SQLException(String.format("The trip %s is not longer valid", tr));
                    }

                    if (t.isBusiness()) {
                        if (!tr.decreaseBusinessSeats(db)) {
                            errorCode.set(NOT_AVAILABLE_SEATS_ERROR_CODE);
                            throw new SQLException("Business seats not available anymore");
                        }
                    } else {
                        if (!tr.decreaseEconomySeats(db)) {
                            errorCode.set(NOT_AVAILABLE_SEATS_ERROR_CODE);
                            throw new SQLException("Economy seats not available anymore");
                        }
                    }

                    if(!currentUsedPromo.contains(p) && SQLiteTicket.hasUserUtilizedPromotion(db,user,p))
                        throw new SQLException("Promo already used for this user, cannot buy tickets");
                    currentUsedPromo.add(p);
                    t.insertRecord(db);
                }
            });
            errorCode.set(SUCCESS_CODE);
            server.updateTicketsCache();
        } catch (SQLException e) {
            logger.warn("Error adding buyed tickets to database: {}",e.getMessage());
        }

        b.setErrorCode(errorCode.get());
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override
    public void bookTickets(BookTicketsRequest request, StreamObserver<BookTicketsReply> responseObserver) {

        BookTicketsReply.Builder b = BookTicketsReply.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            logger.debug("Invalid token request on bookTickets request. Token -> {}",request.getToken().getToken());
            b.setErrorCode(INVALID_TOKEN_ERROR_CODE);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }

        Collection<SQLiteTicket> tickets = request.getTicketsList().stream()
                .map(GrpcConverter::convert)
                .map((t) -> toSanitizedTicket(t,user,false))
                .map(SQLiteTicket::new)
                .toList();

        AtomicInteger errorCode = new AtomicInteger(GENERIC_ERROR_CODE);
        try {
            Collection<Promotion> currentUsedPromo = new LinkedList<>();
            db.atomicTransaction(() -> {
                for(SQLiteTicket t : tickets) {

                    Promotion p = t.getPromotion();

                    // Check if the promotion still exists
                    if (p != null && new SQLitePromotion(p).getRecord(db) == null) {
                        errorCode.set(CANCELLED_PROMOTION);
                        throw new SQLException(String.format("The promotion %s is not longer valid", p.getName()));
                    }

                    SQLiteTrip tr = new SQLiteTrip(t.getTrip());

                    // Check if the trip still exists
                    if (tr.getRecord(db) == null) {
                        errorCode.set(CANCELLED_TRIP);
                        throw new SQLException(String.format("The trip %s is not longer valid", tr));
                    }

                    if (t.isBusiness()) {
                        if (!tr.decreaseBusinessSeats(db)) {
                            errorCode.set(NOT_AVAILABLE_SEATS_ERROR_CODE);
                            throw new SQLException("Business seats not available anymore");
                        }
                    } else {
                        if (!tr.decreaseEconomySeats(db)) {
                            errorCode.set(NOT_AVAILABLE_SEATS_ERROR_CODE);
                            throw new SQLException("Economy seats not available anymore");
                        }
                    }

                    if(!currentUsedPromo.contains(p) && SQLiteTicket.hasUserUtilizedPromotion(db,user,p))
                        throw new SQLException("Promo already used for this user, cannot book tickets");
                    currentUsedPromo.add(p);
                    t.insertRecord(db);
                }
            });
            errorCode.set(SUCCESS_CODE);
            server.updateTicketsCache();
        } catch (SQLException e) {
            logger.warn("Error adding booked tickets to database: {}",e.getMessage());
        }

        b.setErrorCode(errorCode.get());
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override
    public void payBookedTickets(PayBookedTicketsRequest request, StreamObserver<PayBookedTicketsReply> responseObserver) {

        PayBookedTicketsReply.Builder b = PayBookedTicketsReply.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            logger.debug("Invalid token request on payBookedTickets request. Token -> {}",request.getToken().getToken());
            b.setErrorCode(INVALID_TOKEN_ERROR_CODE);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }

        Collection<SQLiteTicket> tickets = request.getTicketsList().stream()
                .map(GrpcConverter::convert)
                .map((t) -> TicketData.newBuilder(t.getId()).setUser(t.getUser()).build())
                .map(SQLiteTicket::new)
                .toList();

        AtomicInteger errorCode = new AtomicInteger(GENERIC_ERROR_CODE);
        try {
            db.atomicTransaction(() -> {
                for (SQLiteTicket t : tickets) {
                    SQLiteTicket tt = t.getRecord(db);
                    if (tt == null || !tt.getUser().getEmail().equals(user.getEmail()) || tt.isPaid()) {
                        errorCode.set(INVALID_TICKET_ERROR_CODE);
                        throw new SQLException("Given tickets are invalid");
                    }
                    tt.updatePaidRecord(db,true);
                }
            });
            errorCode.set(SUCCESS_CODE);
            server.updateTicketsCache();
        } catch (SQLException e) {
            logger.warn("Error paying booked tickets in database: {}",e.getMessage());
        }

        b.setErrorCode(errorCode.get());
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override
    public void editTicket(EditTicketRequest request, StreamObserver<EditTicketReply> responseObserver) {

        EditTicketReply.Builder b = EditTicketReply.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            logger.debug("Invalid token request on editTicket request. Token -> {}",request.getToken().getToken());
            b.setErrorCode(INVALID_TOKEN_ERROR_CODE);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }

        SQLiteTicket ticket = new SQLiteTicket(GrpcConverter.convert(request.getTicket()));

        AtomicInteger errorCode = new AtomicInteger(GENERIC_ERROR_CODE);
        try {
            db.atomicTransaction(() -> {
                SQLiteTicket tt = ticket.getRecord(db);
                if (tt == null || !tt.getUser().getEmail().equals(user.getEmail())) {
                    errorCode.set(INVALID_TICKET_ERROR_CODE);
                    throw new SQLException("Given ticket is invalid");
                }
                if (tt.isBusiness() == ticket.isBusiness()) {
                    errorCode.set(NO_CHANGE_ERROR_CODE);
                    throw new SQLException("Given ticket is the same as the one already in database");
                }
                SQLiteTrip tr = new SQLiteTrip(ticket.getTrip());
                if (ticket.isBusiness()) {
                    if (!tr.increaseEconomySeats(db) || !tr.decreaseBusinessSeats(db)) {
                        errorCode.set(NOT_AVAILABLE_SEATS_ERROR_CODE);
                        throw new SQLException("Business seats not available anymore");
                    }
                } else {
                    if (!tr.increaseBusinessSeats(db) || !tr.decreaseEconomySeats(db)) {
                        errorCode.set(NOT_AVAILABLE_SEATS_ERROR_CODE);
                        throw new SQLException("Economy seats not available anymore");
                    }
                }
                tt.updateBusinessRecord(db, ticket.isBusiness());
            });
            errorCode.set(SUCCESS_CODE);
            server.updateTicketsCache();
        } catch (SQLException e) {
            logger.warn("Error editing ticket in database: {}",e.getMessage());
        }

        b.setErrorCode(errorCode.get());
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override
    public void becomeFidelity(BecomeFidelityRequest request, StreamObserver<BecomeFidelityReply> responseObserver) {

        BecomeFidelityReply.Builder b = BecomeFidelityReply.newBuilder();

        int errorCode = updateFidelity(request.getToken(), true);

        b.setErrorCode(errorCode);
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override
    public void cancelFidelity(CancelFidelityRequest request, StreamObserver<CancelFidelityReply> responseObserver) {

        CancelFidelityReply.Builder b = CancelFidelityReply.newBuilder();

        int errorCode = updateFidelity(request.getToken(), false);

        b.setErrorCode(errorCode);
        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    private int updateFidelity(it.trenical.grpc.SessionToken token, boolean isFidelity) {

        User user = tokenManager.getUser(GrpcConverter.convert(token));
        if(user == null) {
            logger.debug("Invalid token request on {} request. Token -> {}",
                    isFidelity ? "becomeFidelity" : "cancelFidelity",
                    token.getToken()
            );
            return INVALID_TOKEN_ERROR_CODE;
        }

        AtomicInteger errorCode = new AtomicInteger(GENERIC_ERROR_CODE);

        SQLiteUser u = new SQLiteUser(new UserData(user.getEmail(),null,isFidelity));
        try {
            db.atomicTransaction(() -> {
                if (u.getRecord(db).isFidelity() == isFidelity) {
                    errorCode.set(NO_CHANGE_ERROR_CODE);
                    throw new SQLException("Given user has the same fidelity state as the one already in database");
                }
                new SQLiteUser(new UserData(user.getEmail(),null,isFidelity)).updateRecord(db);
            });
            errorCode.set(SUCCESS_CODE);
            server.updateUsersCache();
            logger.info("User {} {} Fidelity program.", user.getEmail(), isFidelity ? "subscribed to" : "unsubscribed from");
        } catch (SQLException e) {
            logger.warn("Error editing fidelity user in database: {}",e.getMessage());
        }

        return errorCode.get();
    }

    private Ticket toSanitizedTicket(Ticket t, User user, boolean isPaid) {
        return TicketData.newBuilder(-1)
                .setUser(user)
                .setName(t.getName())
                .setSurname(t.getSurname())
                .setPromotion(t.getPromotion())
                .setTrip(t.getTrip())
                .setBusiness(t.isBusiness())
                .setPaid(isPaid)
                .setPrice(t.calculatePrice(priceDistanceMultiplier, priceBusinessMultiplier))
                .build();
    }

}
