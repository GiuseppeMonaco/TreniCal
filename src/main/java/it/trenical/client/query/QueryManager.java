package it.trenical.client.query;

import it.trenical.client.auth.SessionToken;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.Station;
import it.trenical.common.Ticket;
import it.trenical.common.TrainType;
import it.trenical.common.Trip;

import java.util.Collection;

public interface QueryManager {

    /**
     * Query trips using the parameter as a filter. If a field of trip is null, any value of that field will be queried
     *
     * @param trip the trip used as filter
     * @return a filtered collection containing trips
     * @throws UnreachableServer if server is unreachable
     */
    Collection<Trip> queryTrips(Trip trip) throws UnreachableServer;

    /**
     * Query all trips available from server
     * @return a collection containing trips
     * @throws UnreachableServer if server is unreachable
     */
    default Collection<Trip> queryTrips() throws UnreachableServer {
        return queryTrips(null);
    }

    /**
     * Query all train types available from server
     * @return a collection containing train types
     * @throws UnreachableServer if server is unreachable
     */
    Collection<TrainType> queryTrainTypes() throws UnreachableServer;

    /**
     * Query all stations available from server
     * @return a collection containing stations
     * @throws UnreachableServer if server is unreachable
     */
    Collection<Station> queryStations() throws UnreachableServer;

    /**
     * Query all tickets of a user
     * @param token the token of the user
     * @return a collection containing user's ticket
     * @throws UnreachableServer if server is unreachable
     */
    Collection<Ticket> queryTickets(SessionToken token) throws UnreachableServer, InvalidSessionTokenException;

}
