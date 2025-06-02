package it.trenical.server.notifications;

import io.grpc.stub.StreamObserver;
import it.trenical.common.Promotion;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.grpc.*;
import it.trenical.server.Server;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.SQLiteNotifiableFidelityUser;
import it.trenical.server.observer.PromotionsCache;
import it.trenical.server.observer.TicketsCache;
import it.trenical.server.observer.TripsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static it.trenical.grpcUtil.GrpcConverter.convert;

public enum NotificationManager implements TicketsCache.Observer, TripsCache.Observer, PromotionsCache.Observer {
    INSTANCE;

    private final Server server;

    private static final Logger logger = LoggerFactory.getLogger(NotificationManager.class);

    private final DatabaseConnection db = Server.INSTANCE.getDatabase();

    private final Map<User, StreamObserver<TicketStream>> almostExpiredBookingUsers = new ConcurrentHashMap<>();
    private final Map<User, StreamObserver<TicketStream>> expiredBookingUsers = new ConcurrentHashMap<>();
    private final Map<User, StreamObserver<TripStream>> tripsDeleteUsers = new ConcurrentHashMap<>();
    private final Map<User, StreamObserver<PromotionStream>> fidelityPromotionsUsers = new ConcurrentHashMap<>();

    private Collection<Ticket> currentTicketsList;
    private Collection<Trip> currentTripsList;
    private Collection<Promotion> currentPromotionsList;

    NotificationManager() {
        this.server = Server.INSTANCE;
        server.ticketsCacheObs.attach(this);
        server.tripsCacheObs.attach(this);
        server.promotionsCacheObs.attach(this);
    }

    public void addAlmostExpiredBookingUser(User user, StreamObserver<TicketStream> observer) {
        almostExpiredBookingUsers.put(user,observer);
    }
    public void addExpiredBookingUser(User user, StreamObserver<TicketStream> observer) {
        expiredBookingUsers.put(user,observer);
    }
    public void addTripsDeleteUser(User user, StreamObserver<TripStream> observer) {
        tripsDeleteUsers.put(user,observer);
    }
    public void addFidelityPromotionsUser(User user, StreamObserver<PromotionStream> observer) {
        fidelityPromotionsUsers.put(user,observer);
    }
    public boolean removeAlmostExpiredBookingUser(User user) {
        return almostExpiredBookingUsers.remove(user) != null;
    }
    public boolean removeExpiredBookingUser(User user) {
        return expiredBookingUsers.remove(user) != null;
    }
    public boolean removeTripsDeleteUser(User user) {
        return tripsDeleteUsers.remove(user) != null;
    }
    public boolean removeFidelityPromotionsUser(User user) {
        return fidelityPromotionsUsers.remove(user) != null;
    }

    public boolean isFidelityUserSubscribed(User user) {
        try {
            return new SQLiteNotifiableFidelityUser(user).getRecord(db) != null;
        } catch (SQLException e) {
            logger.warn("Error checking in Fidelity user is subscribed to newsletter: {}", e.getMessage());
            return false;
        }
    }

    public boolean subscribeFidelityUser(User user) {
        try {
            new SQLiteNotifiableFidelityUser(user).insertRecordIfNotExists(db);
            return true;
        } catch (SQLException e) {
            logger.warn("Error adding Fidelity user subscription to newsletter: {}", e.getMessage());
            return false;
        }
    }

    public boolean unsubscribeFidelityUser(User user) {
        try {
            new SQLiteNotifiableFidelityUser(user).deleteRecord(db);
            return true;
        } catch (SQLException e) {
            logger.warn("Error deleting Fidelity user subscription to newsletter: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void updateTicketsCache() {
        if(currentTicketsList == null) {
            currentTicketsList = new LinkedList<>(server.getTicketsCache());
            return;
        }
        Collection<Ticket> newTicketsList = new LinkedList<>(server.getTicketsCache());

        currentTicketsList.stream().filter(t -> !t.isPaid() && !newTicketsList.contains(t)).forEach(delBooking -> {
            TicketStream ts = TicketStream.newBuilder()
                    .setWasTokenValid(true)
                    .setTimestamp(System.currentTimeMillis())
                    .setTicket(convert(delBooking))
                    .build();
            StreamObserver<TicketStream> stream = expiredBookingUsers.get(delBooking.getUser());
            if (stream != null) stream.onNext(ts);
        });
        currentTicketsList = newTicketsList;
    }

    @Override
    public void updateTripsCache() {
        if(currentTripsList == null) {
            currentTripsList = new LinkedList<>(server.getTripsCache());
            return;
        }
        Collection<Trip> newTripsList = new LinkedList<>(server.getTripsCache());

        currentTripsList.stream().filter(t -> !newTripsList.contains(t)).forEach(delTrip -> {
            TripStream ts = TripStream.newBuilder()
                    .setWasTokenValid(true)
                    .setTimestamp(System.currentTimeMillis())
                    .setTrip(convert(delTrip))
                    .build();
            tripsDeleteUsers.forEach((user, stream) -> {
                if(server.getTicketsCache().stream().anyMatch(tk -> tk.getUser().equals(user) && tk.getTrip().equals(delTrip))) {
                    stream.onNext(ts);}
            });
        });
        currentTripsList = newTripsList;
    }

    @Override
    public void updatePromotionsCache() {
        if(currentPromotionsList == null) {
            currentPromotionsList = new LinkedList<>(server.getPromotionsCache());
            return;
        }
        Collection<Promotion> newPromotionsList = new LinkedList<>(server.getPromotionsCache());

        newPromotionsList.stream().filter(p -> p.isOnlyFidelityUser() && !currentPromotionsList.contains(p)).forEach(newPromo -> {
            PromotionStream ps = PromotionStream.newBuilder()
                    .setWasTokenValid(true)
                    .setTimestamp(System.currentTimeMillis())
                    .setPromotion(convert(newPromo))
                    .build();
            fidelityPromotionsUsers.forEach((user,stream) -> {
                if(isFidelityUserSubscribed(user)) stream.onNext(ps);
            });
        });
        currentPromotionsList = newPromotionsList;
    }
}
