package it.trenical.server.query;

import io.grpc.stub.StreamObserver;
import it.trenical.client.auth.SessionToken;
import it.trenical.common.*;
import it.trenical.common.Promotion;
import it.trenical.common.Station;
import it.trenical.common.Ticket;
import it.trenical.common.TrainType;
import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.grpc.*;
import it.trenical.server.Server;
import it.trenical.server.auth.BiMapTokenManager;
import it.trenical.server.auth.TokenManager;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcQueryImpl extends QueryServiceGrpc.QueryServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(GrpcQueryImpl.class);

    private final DatabaseConnection db = Server.INSTANCE.getDatabase();

    private final TokenManager tokenManager = BiMapTokenManager.INSTANCE;

    @Override
    public void queryTrips(QueryTripsRequest request, StreamObserver<QueryTripsResponse> responseObserver) {

        SQLiteTrip trip = new SQLiteTrip(GrpcConverter.convert(request.getTrip()));
        boolean all = request.getAll();

        Collection<Trip> ret;
        try {
            if(all)
                ret = trip.getAllRecords(db);
            else
                ret = trip.getSimilarRecords(db);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            ret = new ArrayList<>();
        }

        QueryTripsResponse reply = QueryTripsResponse.newBuilder()
                .addAllTrips(ret.stream().map(GrpcConverter::convert).toList())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void queryTrainTypes(QueryTrainTypesRequest request, StreamObserver<QueryTrainTypesResponse> responseObserver) {

        Collection<TrainType> ret;
        try {
            ret = new SQLiteTrainType("").getAllRecords(db);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            ret = new ArrayList<>();
        }

        QueryTrainTypesResponse reply = QueryTrainTypesResponse.newBuilder()
                .addAllTypes(ret.stream().map(GrpcConverter::convert).toList())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void queryStations(QueryStationsRequest request, StreamObserver<QueryStationsResponse> responseObserver) {

        Collection<Station> ret;
        try {
            ret = new SQLiteStation("").getAllRecords(db);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            ret = new ArrayList<>();
        }

        QueryStationsResponse reply = QueryStationsResponse.newBuilder()
                .addAllStations(ret.stream().map(GrpcConverter::convert).toList())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void queryTickets(QueryTicketsRequest request, StreamObserver<QueryTicketsResponse> responseObserver) {

        SessionToken token = new SessionToken(request.getToken());
        User user = tokenManager.getUser(token);

        if(user == null) {
            QueryTicketsResponse reply = QueryTicketsResponse.newBuilder()
                    .setWasTokenValid(false)
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            return;
        }

        Collection<Ticket> ret;
        try {
            ret = new SQLiteTicket(-1,user.getEmail()).getSimilarRecords(db);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            ret = new ArrayList<>();
        }

        QueryTicketsResponse reply = QueryTicketsResponse.newBuilder()
                .setWasTokenValid(true)
                .addAllTickets(ret.stream().map(GrpcConverter::convert).toList())
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void queryUser(QueryUserRequest request, StreamObserver<QueryUserResponse> responseObserver) {

        QueryUserResponse.Builder b = QueryUserResponse.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        User userDb = null;
        try {
            userDb = new SQLiteUser(user).getRecord(db);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }

        if (userDb != null) b.setUser(GrpcConverter.convert(userDb));

        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }

    @Override // TODO aggiungere nella risposta se la promozione è stata già utilizzata
    public void queryPromotion(QueryPromotionRequest request, StreamObserver<QueryPromotionResponse> responseObserver) {

        QueryPromotionResponse.Builder b = QueryPromotionResponse.newBuilder();

        User user = tokenManager.getUser(GrpcConverter.convert(request.getToken()));
        if(user == null) {
            b.setWasTokenValid(false);
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
            return;
        }
        b.setWasTokenValid(true);

        SQLitePromotion promo = new SQLitePromotion(GrpcConverter.convert(request.getPromotion()));
        AtomicReference<Promotion> dbPromotion = new AtomicReference<>();
        try {
            db.atomicTransaction(() -> {
                Promotion tempPromo= promo.getRecord(db);
                if(SQLiteTicket.hasUserUtilizedPromotion(db,user,tempPromo)) return;
                dbPromotion.set(tempPromo);
            });
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }

        if (dbPromotion.get() != null) b.setPromotion(GrpcConverter.convert(dbPromotion.get()));

        responseObserver.onNext(b.build());
        responseObserver.onCompleted();
    }
}
