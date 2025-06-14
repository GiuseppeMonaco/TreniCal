package it.trenical.client.query;

import io.grpc.StatusRuntimeException;
import it.trenical.common.SessionToken;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.GrpcConnection;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.Promotion;
import it.trenical.common.Station;
import it.trenical.common.Ticket;
import it.trenical.common.TrainType;
import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.grpc.*;

import java.util.Collection;

import it.trenical.grpcUtil.GrpcConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcQueryManager implements QueryManager {

    private static final Logger logger = LoggerFactory.getLogger(GrpcQueryManager.class);

    private final QueryServiceGrpc.QueryServiceBlockingStub blockingStub;

    public GrpcQueryManager() {
        blockingStub = QueryServiceGrpc.newBlockingStub(GrpcConnection.getChannel());
    }

    /**
     * Query trips using the parameter as a filter. If a field of trip is null, any value of that field will be queried
     *
     * @param trip the trip used as filter
     * @return a filtered collection containing trips
     */
    @Override
    public Collection<Trip> queryTrips(Trip trip) throws UnreachableServer {

        QueryTripsRequest request = QueryTripsRequest.newBuilder()
                .setTrip(GrpcConverter.convert(trip))
                .setAll(trip == null)
                .build();

        try {
            return blockingStub.queryTrips(request).getTripsList().stream().map(GrpcConverter::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying queryTrips");
            throw new UnreachableServer("Unreachable server");
        }
    }

    /**
     * Query all train types available from server
     *
     * @return a collection containing train types
     */
    @Override
    public Collection<TrainType> queryTrainTypes() throws UnreachableServer {

        QueryTrainTypesRequest request = QueryTrainTypesRequest.getDefaultInstance();

        try {
            return blockingStub.queryTrainTypes(request).getTypesList().stream().map(GrpcConverter::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying queryTrainTypes");
            throw new UnreachableServer("Unreachable server");
        }

    }

    /**
     * Query all stations available from server
     *
     * @return a collection containing stations
     */
    @Override
    public Collection<Station> queryStations() throws UnreachableServer {

        QueryStationsRequest request = QueryStationsRequest.getDefaultInstance();

        try {
            return blockingStub.queryStations(request).getStationsList().stream().map(GrpcConverter::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying queryStations");
            throw new UnreachableServer("Unreachable server");
        }

    }

    /**
     * Query all tickets of a user
     *
     * @param token the token of the user
     * @return a collection containing user's ticket
     */
    @Override
    public Collection<Ticket> queryTickets(SessionToken token) throws UnreachableServer, InvalidSessionTokenException {

        QueryTicketsRequest request = QueryTicketsRequest.newBuilder().setToken(token.token()).build();

        try {
            QueryTicketsResponse reply = blockingStub.queryTickets(request);
            if(!reply.getWasTokenValid()) throw new InvalidSessionTokenException("Given token is invalid");
            return reply.getTicketsList().stream().map(GrpcConverter::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying queryTickets");
            throw new UnreachableServer("Unreachable server");
        }
    }

    /**
     * Query the user associated with a given token
     *
     * @param token the token of the user
     * @return the user
     * @throws UnreachableServer            if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     */
    @Override
    public User queryUser(SessionToken token) throws UnreachableServer, InvalidSessionTokenException {

        QueryUserRequest request = QueryUserRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .build();

        try {
            QueryUserResponse reply = blockingStub.queryUser(request);
            if(!reply.getWasTokenValid()) throw new InvalidSessionTokenException("Given token is invalid");
            return GrpcConverter.convert(reply.getUser());
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying queryUser");
            throw new UnreachableServer("Unreachable server");
        }
    }

    @Override
    public Promotion queryPromotion(SessionToken token, Promotion promotion) throws UnreachableServer, InvalidSessionTokenException {

        QueryPromotionRequest request = QueryPromotionRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .setPromotion(GrpcConverter.convert(promotion))
                .build();

        try {
            QueryPromotionResponse reply = blockingStub.queryPromotion(request);
            if(!reply.getWasTokenValid()) throw new InvalidSessionTokenException("Given token is invalid");
            return GrpcConverter.convert(reply.getPromotion());
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying queryPromotion");
            throw new UnreachableServer("Unreachable server");
        }
    }

    /**
     * Query the multipliers needed to calculate the price of a ticket
     *
     * @return a record that contains the multipliers (float)
     * @throws UnreachableServer if server is unreachable
     */
    @Override
    public PriceMultipliers queryPriceData() throws UnreachableServer {

        QueryPriceDataRequest request = QueryPriceDataRequest.newBuilder().build();

        try {
            QueryPriceDataResponse reply = blockingStub.queryPriceData(request);
            return new PriceMultipliers(
                    reply.getDistanceMultiplier(),
                    reply.getBusinessMultiplier()
            );
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying queryPriceData");
            throw new UnreachableServer("Unreachable server");
        }
    }


}
