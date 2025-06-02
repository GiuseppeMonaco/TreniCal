package it.trenical.client.notification;

import it.trenical.common.Ticket;

public class ExpiredBookingNotification extends Notification {

    private final Ticket booking;

    public ExpiredBookingNotification(long timestamp, Ticket booking) {
        super(timestamp);
        if(booking == null) throw new IllegalArgumentException("booking cannot be null");
        this.booking = booking;
    }

    public Ticket getBooking() {
        return booking;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpiredBookingNotification that)) return false;
        return booking.equals(that.booking);
    }

    @Override
    public int hashCode() {
        return booking.hashCode();
    }
}
