package it.trenical.client.query;

import io.grpc.StatusRuntimeException;
import it.trenical.client.auth.SessionToken;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.GrpcConnection;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.*;
import it.trenical.common.Station;
import it.trenical.common.Ticket;
import it.trenical.common.TrainType;
import it.trenical.common.Trip;
import it.trenical.grpc.*;

import java.util.Collection;
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
}
