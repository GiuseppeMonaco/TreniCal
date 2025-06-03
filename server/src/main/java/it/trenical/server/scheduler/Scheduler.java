package it.trenical.server.scheduler;

import it.trenical.common.Ticket;
import it.trenical.common.Trip;
import it.trenical.server.Server;
import it.trenical.server.notifications.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum Scheduler {
    INSTANCE;

    private static final int HOURS_BEFORE_EXPIRE = 12;
    private static final int HOURS_BEFORE_EXPIRE_ALERT = 24;

    private static final int SCHEDULER_RATE = 60;
    private static final int SCHEDULER_START_DELAY = 5;
    private static final TimeUnit SCHEDULER_RATE_TIMEUNIT = TimeUnit.SECONDS;

    final Server server = Server.INSTANCE;

    final NotificationManager notificationManager = NotificationManager.INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startSchedules() {
        startExpireTask();
        startTripsCleanupTask();
    }

    public void startExpireTask() {
        Runnable task = () -> {
            Calendar now = Calendar.getInstance();
            Collection<Ticket> tickets = new LinkedList<>(server.getTicketsCache());
            tickets.stream().filter(t -> !t.isPaid()).forEach(book -> {

                Calendar departure = book.getTrip().getDepartureTime();
                departure.add(Calendar.HOUR, -HOURS_BEFORE_EXPIRE);
                if (now.after(departure)) {
                    logger.info("Deleting expired booking: {}", book);
                    server.deleteTicket(book);
                    return;
                }

                departure = book.getTrip().getDepartureTime();
                departure.add(Calendar.HOUR, -HOURS_BEFORE_EXPIRE_ALERT);
                if (now.after(departure)) {
                    notificationManager.alertBookExpire(book);
                }
            });
        };
        scheduler.scheduleAtFixedRate(task, SCHEDULER_START_DELAY, SCHEDULER_RATE, SCHEDULER_RATE_TIMEUNIT);
        logger.info("Started bookingExpire scheduler");
    }

    public void startTripsCleanupTask() {
        Runnable task = () -> {
            Calendar now = Calendar.getInstance();
            Collection<Trip> trips = new LinkedList<>(server.getTripsCache());
            trips.forEach(trip -> {

                Calendar departure = trip.getDepartureTime();
                if (now.after(departure)) {
                    logger.info("Deleting departed trip: {}", trip);
                    server.deleteTrip(trip);
                }
            });
        };
        scheduler.scheduleAtFixedRate(task, SCHEDULER_START_DELAY, SCHEDULER_RATE, SCHEDULER_RATE_TIMEUNIT);
        logger.info("Started cleanup scheduler");
    }

    public void shutdown() {
        scheduler.shutdown();
        logger.info("Stopped scheduler");
    }

}
