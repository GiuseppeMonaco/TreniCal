package it.trenical.client.query;

import io.grpc.StatusRuntimeException;
import it.trenical.client.auth.SessionToken;
import it.trenical.client.connection.GrpcConnection;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.*;
import it.trenical.common.Promotion;
import it.trenical.common.Route;
import it.trenical.common.Station;
import it.trenical.common.Ticket;
import it.trenical.common.Train;
import it.trenical.common.TrainType;
import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.grpc.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Logger;

public class GrpcQueryManager implements QueryManager {

    private static final Logger logger = Logger.getLogger(GrpcQueryManager.class.getName());

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

        QueryTripsRequest request = QueryTripsRequest.newBuilder().setTrip(convert(trip)).build();

        try {
            return blockingStub.queryTrips(request).getTripsList().stream().map(this::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warning("Server irraggiungibile");
            throw new UnreachableServer("Server irraggiungibile");
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
            return blockingStub.queryTrainTypes(request).getTypesList().stream().map(this::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warning("Server irraggiungibile");
            throw new UnreachableServer("Server irraggiungibile");
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
            return blockingStub.queryStations(request).getStationsList().stream().map(this::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warning("Server irraggiungibile");
            throw new UnreachableServer("Server irraggiungibile");
        }

    }

    /**
     * Query all tickets of a user
     *
     * @param token the token of the user
     * @return a collection containing user's ticket
     */
    @Override
    public Collection<Ticket> queryTickets(SessionToken token) throws UnreachableServer {

        QueryTicketsRequest request = QueryTicketsRequest.getDefaultInstance();

        try {
            return blockingStub.queryTickets(request).getTicketsList().stream().map(this::convert).toList();
        } catch (StatusRuntimeException e) {
            logger.warning("Server irraggiungibile");
            throw new UnreachableServer("Server irraggiungibile");
        }
    }

    // Converter methods //

    private User convert(it.trenical.grpc.User user) {
        if(user == null) throw new IllegalArgumentException("user cannot be null");
        return new UserData(
                user.getEmail(),
                (user.hasPassword() ? user.getPassword() : null),
                user.getIsFidelity()
        );
    }
    private it.trenical.grpc.User convert(User user) {
        it.trenical.grpc.User.Builder b = it.trenical.grpc.User.newBuilder();
        if(user == null) return b.build();
        b.setEmail(user.getEmail());
        if(user.getPassword() != null) b.setEmail(user.getPassword());
        b.setIsFidelity(user.isFidelity());
        return b.build();
    }
    private Promotion convert(it.trenical.grpc.Promotion promotion) {
        if(promotion == null) throw new IllegalArgumentException("promotion cannot be null");
        PromotionData.Builder b = PromotionData.newBuilder(promotion.getCode());
        if(promotion.hasName())b.setName(promotion.getName());
        if(promotion.hasDescription()) b.setDescription(promotion.getDescription());
        b.setOnlyFidelityUser(promotion.getIsOnlyFidelityUser());
        b.setDiscount(promotion.getDiscount());
        return b.build();
    }
    private it.trenical.grpc.Promotion convert(Promotion promotion) {
        it.trenical.grpc.Promotion.Builder b = it.trenical.grpc.Promotion.newBuilder();
        if(promotion == null) return b.build();
        if(promotion.getCode() != null) b.setName(promotion.getName());
        if(promotion.getDescription() != null) b.setDescription(promotion.getDescription());
        b.setIsOnlyFidelityUser(promotion.isOnlyFidelityUser());
        b.setDiscount(promotion.getDiscount());
        return b.build();
    }
    private Ticket convert(it.trenical.grpc.Ticket ticket) {
        if(ticket == null) throw new IllegalArgumentException("ticket cannot be null");
        TicketData.Builder b = TicketData.newBuilder(ticket.getId(),convert(ticket.getUser()));
        if(ticket.hasName()) b.setName(ticket.getName());
        if(ticket.hasSurname()) b.setSurname(ticket.getSurname());
        b.setPrice(ticket.getPrice());
        if(ticket.hasPromotion()) b.setPromotion(convert(ticket.getPromotion()));
        if(ticket.hasTrip()) b.setTrip(convert(ticket.getTrip()));
        b.setPaid(ticket.getIsPaid());
        return b.build();
    }
    private it.trenical.grpc.Ticket convert(Ticket ticket) {
        it.trenical.grpc.Ticket.Builder b = it.trenical.grpc.Ticket.newBuilder();
        if(ticket == null) return b.build();
        if(ticket.getName() != null) b.setName(ticket.getName());
        if(ticket.getSurname() != null) b.setSurname(ticket.getSurname());
        b.setPrice(ticket.getPrice());
        if(ticket.getPromotion() != null) b.setPromotion(convert(ticket.getPromotion()));
        if(ticket.getTrip() != null) b.setTrip(convert(ticket.getTrip()));
        b.setIsPaid(ticket.isPaid());
        return b.build();
    }
    private Trip convert(it.trenical.grpc.Trip trip) {
        if(trip == null) throw new IllegalArgumentException("trip cannot be null");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(trip.getDepartureTime());
        TripData.Builder b = TripData.newBuilder(convert(trip.getRoute()));
        if(trip.hasTrain()) b.setTrain(convert(trip.getTrain()));
        if(trip.getDepartureTime() == trip.getDefaultInstanceForType().getDepartureTime()) b.setDepartureTime(c);
        b.setAvailableEconomySeats(trip.getAvailableEconomySeats());
        b.setAvailableBusinessSeats(trip.getAvailableBusinessSeats());
        return b.build();
    }
    private it.trenical.grpc.Trip convert(Trip trip) {
        it.trenical.grpc.Trip.Builder b = it.trenical.grpc.Trip.newBuilder();
        if(trip == null) return b.build();
        if(trip.getTrain() != null) b.setTrain(convert(trip.getTrain()));
        if(trip.getDepartureTime() != null) b.setDepartureTime(trip.getDepartureTime().getTimeInMillis());
        if(trip.getRoute() != null) b.setRoute(convert(trip.getRoute()));
        b.setAvailableEconomySeats(trip.getAvailableEconomySeats());
        b.setAvailableBusinessSeats(trip.getAvailableBusinessSeats());
        return b.build();
    }
    private Train convert(it.trenical.grpc.Train train) {
        if(train == null) throw new IllegalArgumentException("train cannot be null");
        TrainData.Builder b = TrainData.newBuilder(train.getId());
        if(train.hasType()) b.setType(convert(train.getType()));
        b.setEconomyCapacity(train.getEconomyCapacity());
        b.setBusinessCapacity(train.getBusinessCapacity());
        return b.build();
    }
    private it.trenical.grpc.Train convert(Train train) {
        it.trenical.grpc.Train.Builder b = it.trenical.grpc.Train.newBuilder();
        if(train == null) return b.build();
        b.setId(train.getId());
        if(train.getType() != null) b.setType(convert(train.getType()));
        b.setEconomyCapacity(train.getEconomyCapacity());
        b.setBusinessCapacity(train.getBusinessCapacity());
        return b.build();
    }
    private Station convert(it.trenical.grpc.Station station) {
        if(station == null) throw new IllegalArgumentException("station cannot be null");
        StationData.Builder b = StationData.newBuilder(station.getName());
        if(station.hasAddress()) b.setAddress(station.getAddress());
        if(station.hasTown()) b.setTown(station.getTown());
        if(station.hasProvince()) b.setProvince(station.getProvince());
        return b.build();
    }
    private it.trenical.grpc.Station convert(Station station) {
        it.trenical.grpc.Station.Builder b = it.trenical.grpc.Station.newBuilder();
        if(station == null) return b.build();
        b.setName(station.getName());
        if(station.getAddress() != null) b.setAddress(station.getAddress());
        if(station.getTown() != null) b.setTown(station.getTown());
        if(station.getProvince() != null) b.setProvince(station.getProvince());
        return b.build();
    }
    private Route convert(it.trenical.grpc.Route route) {
        if(route == null) throw new IllegalArgumentException("route cannot be null");
        return new RouteData(
                convert(route.getDepartureStation()),
                convert(route.getArrivalStation()),
                route.getDistance()
        );
    }
    private it.trenical.grpc.Route convert(Route route) {
        it.trenical.grpc.Route.Builder b = it.trenical.grpc.Route.newBuilder();
        if(route == null) return b.build();
        b.setDepartureStation(convert(route.getDepartureStation()));
        b.setArrivalStation(convert(route.getArrivalStation()));
        b.setDistance(route.getDistance());
        return b.build();
    }
    private TrainType convert(it.trenical.grpc.TrainType trainType) {
        if(trainType == null) throw new IllegalArgumentException("trainType cannot be null");
        return new TrainTypeData(trainType.getName(), trainType.getPrice());
    }
    private it.trenical.grpc.TrainType convert(TrainType trainType) {
        it.trenical.grpc.TrainType.Builder b = it.trenical.grpc.TrainType.newBuilder();
        if(trainType == null) return b.build();
        b.setName(trainType.getName());
        b.setPrice(trainType.getPrice());
        return b.build();
    }
}
