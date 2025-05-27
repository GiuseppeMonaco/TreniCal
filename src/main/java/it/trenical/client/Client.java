package it.trenical.client;

import it.trenical.client.auth.AuthManager;
import it.trenical.client.auth.GrpcAuthManager;
import it.trenical.client.auth.SessionToken;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.*;
import it.trenical.client.query.GrpcQueryManager;
import it.trenical.client.query.QueryManager;
import it.trenical.common.*;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    // Singleton class
    private static Client instance;

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private User currentUser;
    private SessionToken token;

    private final AuthManager auth;
    private final QueryManager query;

    // Subjects classes //
    public final Login.Subject loginSub = new LoginSubject();
    public final Logout.Subject logoutSub = new LogoutSubject();

    private final Collection<Station> stationsCache = new LinkedList<>();
    public final StationsCache.Subject stationsCacheSub = new StationsCacheSubject(stationsCache);

    private final Collection<Trip> tripsCache = new LinkedList<>();
    public final TripsCache.Subject tripsCacheSub = new TripsCacheSubject(tripsCache);

    private final Collection<Trip> filteredTripsCache = new LinkedList<>();
    public final TripsCache.Subject filteredTripsCacheSub = new TripsCacheSubject(filteredTripsCache);

    private final Collection<TrainType> trainTypesCache = new LinkedList<>();
    public final TrainTypesCache.Subject trainTypesCacheSub = new TrainTypesCacheSubject(trainTypesCache);

    private final Collection<Ticket> ticketsCache = new LinkedList<>();
    public final TicketsCache.Subject ticketsCacheSub = new TicketsCacheSubject(ticketsCache);

    public final FidelityUser.Subject fidelityUserSub = new FidelityUserSubject(currentUser);

    private Client() {
        auth = new GrpcAuthManager();
        query = new GrpcQueryManager();
    }

    public static synchronized Client getInstance() {
        if (instance == null) instance = new Client();
        return instance;
    }

    public void login(User user) throws InvalidCredentialsException, UnreachableServer {
        token = auth.login(user);
        try {
            updateCurrentUser();
        } catch (InvalidSessionTokenException e) {
            logger.error("Critical error during login, aborting. Please contact software developer");
            System.exit(-1);
        }
        loginSub.notifyObs();
        logger.info("Login effettuato come {}", user.getEmail());
    }

    public void logout() throws UnreachableServer {
        try {
            auth.logout(token);
            logger.info("Logout effettuato");
        } catch (InvalidSessionTokenException e) {
            logger.warn("Token was invalid: {}", token.token());
        }
        token = null;
        currentUser = null;
        logoutSub.notifyObs();
    }

    public void signup(User user) throws InvalidCredentialsException, UserAlreadyExistsException, UnreachableServer {
        token = auth.signup(user);
        try {
            updateCurrentUser();
        } catch (InvalidSessionTokenException e) {
            logger.error("Critical error during signup, aborting. Please contact software developer");
            System.exit(-1);
        }
        logger.info("Signup effettuato come {}", user.getEmail());
        loginSub.notifyObs();
    }

    public boolean isAuthenticated() {
        return token != null && currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void queryStations() throws UnreachableServer {
        stationsCache.clear();
        stationsCache.addAll(query.queryStations());
        stationsCacheSub.notifyObs();
    }

    public void queryTrips() throws UnreachableServer {
        tripsCache.clear();
        tripsCache.addAll(query.queryTrips());
        tripsCacheSub.notifyObs();
    }

    public void queryTrips(Trip trip) throws UnreachableServer {
        filteredTripsCache.clear();
        filteredTripsCache.addAll(query.queryTrips(trip));
        filteredTripsCacheSub.notifyObs();
    }

    public void queryTrainTypes() throws UnreachableServer {
        trainTypesCache.clear();
        trainTypesCache.addAll(query.queryTrainTypes());
        trainTypesCacheSub.notifyObs();
    }

    public void queryTickets() throws UnreachableServer, InvalidSessionTokenException {
        ticketsCache.clear();
        ticketsCache.addAll(query.queryTickets(token));
        ticketsCacheSub.notifyObs();
    }

    public void updateCurrentUser() throws UnreachableServer, InvalidSessionTokenException {
        currentUser = query.queryUser(token);
        fidelityUserSub.notifyObs();
    }

    public static void main(String[] args) throws Exception {
        Client c = Client.getInstance();
        c.login(new UserData("mario.rossi@gmail.com", "passwordbella123"));
        c.queryTickets();
        c.queryStations();
        c.queryTrips();
        c.queryTrainTypes();
        logger.info(c.stationsCache.toString());
        logger.info(c.tripsCache.toString());
        logger.info(c.trainTypesCache.toString());
        logger.info(c.ticketsCache.toString());
    }
}
