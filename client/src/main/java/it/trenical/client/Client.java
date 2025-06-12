package it.trenical.client;

import it.trenical.client.auth.AuthManager;
import it.trenical.client.auth.GrpcAuthManager;
import it.trenical.client.notification.Notification;
import it.trenical.client.notification.NotificationHandler;
import it.trenical.client.query.PriceMultipliers;
import it.trenical.client.request.exceptions.*;
import it.trenical.common.SessionToken;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.*;
import it.trenical.client.query.GrpcQueryManager;
import it.trenical.client.query.QueryManager;
import it.trenical.client.request.GrpcRequestManager;
import it.trenical.client.request.RequestManager;
import it.trenical.common.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    // Singleton class
    private static Client instance;

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static String VALID_EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private User currentUser;
    private SessionToken currentToken;
    private boolean isSubscribedToFidelityPromotion = false;

    private final Collection<Station> stationsCache = new LinkedList<>();
    private final Collection<Trip> tripsCache = new LinkedList<>();
    private final Collection<Trip> filteredTripsCache = new LinkedList<>();
    private final Collection<TrainType> trainTypesCache = new LinkedList<>();
    private final Collection<Ticket> ticketsCache = new LinkedList<>();

    private int currentPassengersNumber = 0;
    private int currentEconomyPassengers = 0;
    private int currentBusinessPassengers = 0;
    private Trip currentTrip;
    private Promotion currentPromotion;
    private float currentTotalPrice;
    private float currentEconomyTicketPrice = 0;
    private float currentBusinessTicketPrice = 0;

    private float priceDistanceMultiplier = 0;
    private float priceBusinessMultiplier = 0;

    private final Collection<Notification> notificationBuffer = new ConcurrentLinkedDeque<>();

    private final AuthManager auth;
    private final QueryManager query;
    private final RequestManager request;
    private final NotificationHandler notification;

    // Subjects classes //
    public final Login.Subject loginSub = new LoginSubject();
    public final Logout.Subject logoutSub = new LogoutSubject();
    public final StationsCache.Subject stationsCacheSub = new StationsCacheSubject();
    public final TripsCache.Subject tripsCacheSub = new TripsCacheSubject();
    public final TripsCache.Subject filteredTripsCacheSub = new TripsCacheSubject();
    public final TrainTypesCache.Subject trainTypesCacheSub = new TrainTypesCacheSubject();
    public final TicketsCache.Subject ticketsCacheSub = new TicketsCacheSubject();
    public final FidelityUser.Subject fidelityUserSub = new FidelityUserSubject();
    public final PassengersNumber.Subject passengersNumberSub = new PassengersNumberSubject();
    public final CurrentTrip.Subject currentTripSub = new CurrentTripSubject();
    public final CurrentPromotion.Subject currentPromoSub = new CurrentPromotionSubject();
    public final CurrentPrice.Subject currentPriceSub = new CurrentPriceSubject();
    public final NotificationChange.Subject notificationChangeSub = new NotificationChangeSubject();

    private Client() {
        auth = new GrpcAuthManager();
        query = new GrpcQueryManager();
        request = new GrpcRequestManager();
        notification = new NotificationHandler();
        loginSub.attach(notification);
        logoutSub.attach(notification);
    }

    public static synchronized Client getInstance() {
        if (instance == null) instance = new Client();
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    public SessionToken getCurrentToken() {
        return currentToken;
    }
    public boolean isSubscribedToFidelityPromotion() {
        return isSubscribedToFidelityPromotion;
    }
    public Collection<Station> getStationsCache() {
        return stationsCache;
    }
    public Collection<Trip> getTripsCache() {
        return tripsCache;
    }
    public Collection<Trip> getFilteredTripsCache() {
        return filteredTripsCache;
    }
    public Collection<TrainType> getTrainTypesCache() {
        return trainTypesCache;
    }
    public Collection<Ticket> getTicketsCache() {
        return ticketsCache;
    }
    public int getCurrentPassengersNumber() {
        return currentPassengersNumber;
    }
    public Trip getCurrentTrip() {
        return currentTrip;
    }
    public Promotion getCurrentPromotion() {
        return currentPromotion;
    }
    public float getCurrentTotalPrice() {
        return currentTotalPrice;
    }
    public float getCurrentEconomyTicketPrice() {
        return currentEconomyTicketPrice;
    }
    public float getCurrentBusinessTicketPrice() {
        return currentBusinessTicketPrice;
    }
    public float getTicketPrice(Ticket t) {
        return t.calculatePrice(priceDistanceMultiplier, priceBusinessMultiplier);
    }

    public void setCurrentPassengersNumber(int number) {
        if (number < 0) throw new IllegalArgumentException("Passengers number cannot be negative");
        currentPassengersNumber = number;
        currentEconomyPassengers = number;
        currentBusinessPassengers = 0;
        passengersNumberSub.notifyObs();
    }
    public void setCurrentTrip(Trip currentTrip) {
        this.currentTrip = currentTrip;
        if (currentTrip != null) {
            currentEconomyTicketPrice = TicketData.newBuilder(-1).setTrip(currentTrip)
                    .setBusiness(false).build().calculatePrice(priceDistanceMultiplier, priceBusinessMultiplier);
            currentBusinessTicketPrice = TicketData.newBuilder(-1).setTrip(currentTrip)
                    .setBusiness(true).build().calculatePrice(priceDistanceMultiplier, priceBusinessMultiplier);
        } else {
            currentEconomyTicketPrice = 0;
            currentBusinessTicketPrice = 0;
        }
        updateCurrentTotalPrice();
        currentTripSub.notifyObs();
    }
    public void setCurrentPromotion(Promotion promotion) throws UnreachableServer {
        Promotion previousPromo = currentPromotion;
        Promotion promo = null;
        if (promotion != null) try {
            promo = queryPromotion(promotion);
        } catch (InvalidSessionTokenException e) {
            logger.warn(e.getMessage());
        }
        currentPromotion = promo;
        if (promo != null && promo.isOnlyFidelityUser() &&  !getCurrentUser().isFidelity()) {
            currentPromotion = null;
        }
        if(previousPromo != null) setCurrentTotalPrice(getCurrentTotalPrice() / previousPromo.getDiscount());
        updateCurrentTotalPrice();
        currentPromoSub.notifyObs();
    }
    public void setCurrentTotalPrice(float price) {
        if(currentPromotion != null) {
            price *= currentPromotion.getDiscount();
        }
        this.currentTotalPrice = price;
        currentPriceSub.notifyObs();
    }

    public void incrementCurrentEconomyPassengers() {
        setCurrentEconomyPassengers(currentEconomyPassengers + 1);
    }
    public void decrementCurrentEconomyPassengers() {
        setCurrentEconomyPassengers(currentEconomyPassengers - 1);
    }
    public void incrementCurrentBusinessPassengers() {
        setCurrentBusinessPassengers(currentBusinessPassengers + 1);
    }
    public void decrementCurrentBusinessPassengers() {
        setCurrentBusinessPassengers(currentBusinessPassengers - 1);
    }
    private void setCurrentEconomyPassengers(int number) {
        if (number < 0) throw new IllegalArgumentException("Passengers number cannot be negative");
        currentEconomyPassengers = number;
        updateCurrentTotalPrice();
    }
    private void setCurrentBusinessPassengers(int number) {
        if (number < 0) throw new IllegalArgumentException("Passengers number cannot be negative");
        currentBusinessPassengers = number;
        updateCurrentTotalPrice();
    }
    private void updateCurrentTotalPrice() {
        setCurrentTotalPrice(currentEconomyTicketPrice * currentEconomyPassengers + currentBusinessTicketPrice * currentBusinessPassengers);
    }

    public void addNotification(Notification notification) {
        notificationBuffer.add(notification);
        notificationChangeSub.notifyObs();
    }
    public void clearNotificationBuffer() {
        notificationBuffer.clear();
        notificationChangeSub.notifyObs();
    }
    public int getNotificationsCount() {
        return notificationBuffer.size();
    }
    public Collection<Notification> getNotifications() {
        return new LinkedList<>(notificationBuffer);
    }

    public void login(User user) throws InvalidCredentialsException, UnreachableServer {
        currentToken = auth.login(user);
        updatePriceMultipliers();
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
            auth.logout(currentToken);
            setCurrentPromotion(null);
            logger.info("Logout effettuato");
        } catch (InvalidSessionTokenException e) {
            logger.warn("Token was invalid: {}", currentToken.token());
        }
        currentToken = null;
        currentUser = null;
        clearNotificationBuffer();
        logoutSub.notifyObs();
    }

    public void signup(User user) throws InvalidCredentialsException, UserAlreadyExistsException, UnreachableServer {
        currentToken = auth.signup(user);
        updatePriceMultipliers();
        try {
            updateCurrentUser();
        } catch (InvalidSessionTokenException e) {
            logger.error("Critical error during signup, aborting. Please contact software developer");
            System.exit(-1);
        }
        loginSub.notifyObs();
        logger.info("Signup effettuato come {}", user.getEmail());
    }

    public boolean isAuthenticated() {
        return currentToken != null && currentUser != null;
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
        updatePriceMultipliers();
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
        try {
            ticketsCache.addAll(query.queryTickets(currentToken));
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
        ticketsCacheSub.notifyObs();
    }

    public Promotion queryPromotion(Promotion promotion) throws UnreachableServer, InvalidSessionTokenException {
        try {
            return query.queryPromotion(currentToken,promotion);
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }

    public void updatePriceMultipliers() throws UnreachableServer {
        PriceMultipliers pm = query.queryPriceData();
        priceDistanceMultiplier = pm.distance();
        priceBusinessMultiplier = pm.business();
    }

    public void updateCurrentUser() throws UnreachableServer, InvalidSessionTokenException {
        try {
            currentUser = query.queryUser(currentToken);
            isSubscribedToFidelityPromotion = notification.isUserSubscribedToFidelityPromotions(currentToken);
            fidelityUserSub.notifyObs();
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }

    public void buyTickets(Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidSeatsNumberException, CancelledTripException, CancelledPromotionException {
        try {
            request.buyTickets(currentToken, tickets);
            queryTickets();
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        } catch (CancelledPromotionException e) {
            setCurrentPromotion(null);
            throw e;
        }
    }
    public void bookTickets(Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidSeatsNumberException, CancelledTripException, CancelledPromotionException {
        try {
            request.bookTickets(currentToken, tickets);
            queryTickets();
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        } catch (CancelledPromotionException e) {
            setCurrentPromotion(null);
            throw e;
        }
    }
    public void payBookedTickets(Collection<Ticket> tickets) throws InvalidTicketException, UnreachableServer, InvalidSessionTokenException {
        try {
            request.payBookedTickets(currentToken, tickets);
            queryTickets();
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }
    public void editTicket(Ticket ticket) throws UnreachableServer, InvalidSessionTokenException, InvalidTicketException, NoChangeException, InvalidSeatsNumberException {
        try {
            request.editTicket(currentToken, ticket);
            queryTickets();
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }
    public void becomeFidelity() throws UnreachableServer, InvalidSessionTokenException, NoChangeException {
        try {
            request.becomeFidelity(currentToken);
            updateCurrentUser();
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }
    public void cancelFidelity() throws UnreachableServer, InvalidSessionTokenException, NoChangeException {
        try {
            request.cancelFidelity(currentToken);
            if(currentPromotion != null && currentPromotion.isOnlyFidelityUser()) setCurrentPromotion(null);
            updateCurrentUser();
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }

    public void subscribeToFidelityPromotions() throws UnreachableServer, InvalidSessionTokenException {
        try {
            if (notification.fidelityPromotionsSubscribe(currentToken)) isSubscribedToFidelityPromotion = true;
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }

    public void unsubscribeFromFidelityPromotions() throws UnreachableServer, InvalidSessionTokenException {
        try {
            if(notification.fidelityPromotionsUnubscribe(currentToken)) isSubscribedToFidelityPromotion = false;
        } catch (InvalidSessionTokenException e) {
            logout();
            throw e;
        }
    }
}
