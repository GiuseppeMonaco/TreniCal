package it.trenical.client.notification;

import it.trenical.common.Trip;

public class TripCancelledNotification extends Notification {

    private final Trip cancelledTrip;

    public TripCancelledNotification(long timestamp, Trip cancelledTrip) {
        super(timestamp);
        if(cancelledTrip == null) throw new IllegalArgumentException("trip cannot be null");
        this.cancelledTrip = cancelledTrip;
    }

    public Trip getCancelledTrip() {
        return cancelledTrip;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripCancelledNotification that)) return false;
        return cancelledTrip.equals(that.cancelledTrip);
    }

    @Override
    public int hashCode() {
        return cancelledTrip.hashCode();
    }
}
