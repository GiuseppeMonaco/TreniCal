package it.trenical.server.notifications;

import io.grpc.stub.StreamObserver;
import it.trenical.common.Promotion;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.grpc.*;
import it.trenical.server.Server;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.*;
import it.trenical.server.observer.PromotionsCache;
import it.trenical.server.observer.TicketsCache;
import it.trenical.server.observer.TripsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
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

    private boolean enableNotificationPersistance = true;

    NotificationManager() {
        this.server = Server.INSTANCE;
        server.ticketsCacheObs.attach(this);
        server.tripsCacheObs.attach(this);
        server.promotionsCacheObs.attach(this);

        initDatabase();
    }

    private void initDatabase() {
        try (Statement st = db.getConnection().createStatement()){
            db.atomicTransaction(() -> {
                SQLiteExpiredBookingNotification.initTable(st);
                SQLiteAlmostExpiredBookingNotification.initTable(st);
                SQLiteCancelledTripNotification.initTable(st);
                SQLiteNewFidelityPromotionNotification.initTable(st);
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
            enableNotificationPersistance = false;
        }
    }

    public void addAlmostExpiredBookingUser(User user, StreamObserver<TicketStream> observer) {
        almostExpiredBookingUsers.put(user,observer);
        getStoredAlmostExpiredBookingNotifications(user).forEach(this::sendAlmostExpiredBookingNotification);
    }
    public void addExpiredBookingUser(User user, StreamObserver<TicketStream> observer) {
        expiredBookingUsers.put(user,observer);
        getStoredExpiredBookingNotifications(user).forEach(this::sendExpiredBookingNotification);
    }
    public void addTripsDeleteUser(User user, StreamObserver<TripStream> observer) {
        tripsDeleteUsers.put(user,observer);
        getStoredTripsDeleteNotifications(user).forEach(this::sendTripsDeleteNotification);
    }
    public void addFidelityPromotionsUser(User user, StreamObserver<PromotionStream> observer) {
        fidelityPromotionsUsers.put(user,observer);
        getStoredFidelityPromotionsNotifications(user).forEach(this::sendFidelityPromotionsNotification);
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

    public void alertBookExpire(Ticket book) {
        sendAlmostExpiredBookingNotification(book);
    }

    @Override
    public void updateTicketsCache() {
        if(currentTicketsList == null) {
            currentTicketsList = new LinkedList<>(server.getTicketsCache());
            return;
        }
        Collection<Ticket> newTicketsList = new LinkedList<>(server.getTicketsCache());
        Calendar now = Calendar.getInstance();

        currentTicketsList.stream().filter(t -> !t.isPaid() && !newTicketsList.contains(t)).forEach(delBooking -> {
            Calendar departure = delBooking.getTrip().getDepartureTime();
            if(now.after(departure)) return; // Check if the booking has been deleted because it's departed and not cancelled
            sendExpiredBookingNotification(delBooking);
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

        currentTripsList.stream().filter(t -> !newTripsList.contains(t)).forEach(this::sendTripsDeleteNotification);
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
            long timestamp = System.currentTimeMillis();
            PromotionStream ps = PromotionStream.newBuilder()
                    .setWasTokenValid(true)
                    .setTimestamp(timestamp)
                    .setPromotion(convert(newPromo))
                    .build();
            sendFidelityPromotionsNotification(ps,newPromo,timestamp);
        });
        currentPromotionsList = newPromotionsList;
    }

    private void sendAlmostExpiredBookingNotification(Ticket book) {
        long timestamp = System.currentTimeMillis();
        TicketStream ts = TicketStream.newBuilder()
                .setWasTokenValid(true)
                .setTimestamp(timestamp)
                .setTicket(convert(book))
                .build();
        User u = book.getUser();
        if (almostExpiredBookingUsers.containsKey(u)) {
            almostExpiredBookingUsers.get(u).onNext(ts);
        } else {
            storeAlmostExpiredBookingNotification(u, book, timestamp);
        }
    }
    private void sendExpiredBookingNotification(Ticket delBooking) {
        long timestamp = System.currentTimeMillis();
        TicketStream ts = TicketStream.newBuilder()
                .setWasTokenValid(true)
                .setTimestamp(timestamp)
                .setTicket(convert(delBooking))
                .build();
        User user = delBooking.getUser();
        if(expiredBookingUsers.containsKey(user)) {
            expiredBookingUsers.get(user).onNext(ts);
        } else {
            storeExpiredBookingNotification(user, delBooking, timestamp);
        }
    }
    private void sendTripsDeleteNotification(Trip delTrip) {
        long timestamp = System.currentTimeMillis();
        TripStream ts = TripStream.newBuilder()
                .setWasTokenValid(true)
                .setTimestamp(timestamp)
                .setTrip(convert(delTrip))
                .build();
        server.getTicketsCache().stream().filter(t -> delTrip.equals(t.getTrip())).forEach(delTk -> {
            User u = delTk.getUser();
            if(tripsDeleteUsers.containsKey(u)) {
                tripsDeleteUsers.get(u).onNext(ts);
            } else {
                storeTripsDeleteNotification(u, delTrip, timestamp);
            }
        });
    }
    private void sendFidelityPromotionsNotification(PromotionStream ps, Promotion promotion, long timestamp) {
        server.getUsersCache().stream().filter(u -> u.isFidelity() && isFidelityUserSubscribed(u)).forEach(u -> {
            if(fidelityPromotionsUsers.containsKey(u)) {
                fidelityPromotionsUsers.get(u).onNext(ps);
            } else {
                storeFidelityPromotionsNotification(u, promotion, timestamp);
            }
        });
    }
    private void sendFidelityPromotionsNotification(Promotion promotion) {
        long timestamp = System.currentTimeMillis();
        PromotionStream ps = PromotionStream.newBuilder()
                .setWasTokenValid(true)
                .setTimestamp(timestamp)
                .setPromotion(convert(promotion))
                .build();
        server.getUsersCache().stream().filter(u -> u.isFidelity() && isFidelityUserSubscribed(u)).forEach(u -> {
            if(fidelityPromotionsUsers.containsKey(u)) {
                fidelityPromotionsUsers.get(u).onNext(ps);
            } else {
                storeFidelityPromotionsNotification(u, promotion, timestamp);
            }
        });
    }

    private void storeExpiredBookingNotification(User user, Ticket booking, long timestamp) {
        if (!enableNotificationPersistance) return;
        try {
            new SQLiteExpiredBookingNotification(user, booking, timestamp).insertRecord(db);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
    private void storeAlmostExpiredBookingNotification(User user, Ticket booking, long timestamp) {
        if (!enableNotificationPersistance) return;
        try {
            new SQLiteAlmostExpiredBookingNotification(user, booking, timestamp).insertRecord(db);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
    private void storeTripsDeleteNotification(User user, Trip trip, long timestamp) {
        if (!enableNotificationPersistance) return;
        try {
            new SQLiteCancelledTripNotification(user, trip, timestamp).insertRecord(db);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
    private void storeFidelityPromotionsNotification(User user, Promotion promotion, long timestamp) {
        if (!enableNotificationPersistance) return;
        try {
            new SQLiteNewFidelityPromotionNotification(user, promotion, timestamp).insertRecord(db);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
    private Collection<Ticket> getStoredExpiredBookingNotifications(User user) {
        Collection<Ticket> ret = new LinkedList<>();
        if (!enableNotificationPersistance) return ret;
        SQLiteExpiredBookingNotification q = new SQLiteExpiredBookingNotification(user, null, -1);
        try {
            db.atomicTransaction(() -> {
                ret.addAll(q.getSimilarRecords(db).stream().map(SQLiteExpiredBookingNotification::book).toList());
                q.deleteRecord(db);
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return ret;
    }
    private Collection<Ticket> getStoredAlmostExpiredBookingNotifications(User user) {
        Collection<Ticket> ret = new LinkedList<>();
        if (!enableNotificationPersistance) return ret;
        SQLiteExpiredBookingNotification q = new SQLiteExpiredBookingNotification(user, null, -1);
        try {
            db.atomicTransaction(() -> {
                ret.addAll(q.getSimilarRecords(db).stream().map(SQLiteExpiredBookingNotification::book).toList());
                q.deleteRecord(db);
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return ret;
    }
    private Collection<Trip> getStoredTripsDeleteNotifications(User user) {
        Collection<Trip> ret = new LinkedList<>();
        if (!enableNotificationPersistance) return ret;
        SQLiteCancelledTripNotification q = new SQLiteCancelledTripNotification(user, null, -1);
        try {
            db.atomicTransaction(() -> {
                ret.addAll(q.getSimilarRecords(db).stream().map(SQLiteCancelledTripNotification::trip).toList());
                q.deleteRecord(db);
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return ret;
    }
    private Collection<Promotion> getStoredFidelityPromotionsNotifications(User user) {
        Collection<Promotion> ret = new LinkedList<>();
        if (!enableNotificationPersistance) return ret;
        SQLiteNewFidelityPromotionNotification q = new SQLiteNewFidelityPromotionNotification(user, null,-1);
        try {
            db.atomicTransaction(() -> {
                ret.addAll(q.getSimilarRecords(db).stream().map(SQLiteNewFidelityPromotionNotification::promotion).toList());
                q.deleteRecord(db);
            });
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return ret;
    }
}
