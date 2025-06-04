package it.trenical.client.query;

import it.trenical.common.SessionToken;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.*;

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

    /**
     * Query the user associated with a given token
     * @param token the token of the user
     * @return the user
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     */
    User queryUser(SessionToken token) throws UnreachableServer, InvalidSessionTokenException;

    /**
     * Query the promotion passed as parameter
     * @param token the token of the user
     * @param promotion the promotion to query
     * @return the promotion if exists, else null
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     */
    Promotion queryPromotion(SessionToken token, Promotion promotion) throws UnreachableServer, InvalidSessionTokenException;

    /**
     * Query the multipliers needed to calculate the price of a ticket
     * @return a record that contains the multipliers (float)
     * @throws UnreachableServer if server is unreachable
     */
    PriceMultipliers queryPriceData() throws UnreachableServer;
}
