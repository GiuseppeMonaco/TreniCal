package it.trenical.client.notification;

public abstract class Notification implements Comparable<Notification> {
    protected final long timestamp;

    public Notification(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Notification notification) {
        return Long.compare(timestamp, notification.timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }
}
