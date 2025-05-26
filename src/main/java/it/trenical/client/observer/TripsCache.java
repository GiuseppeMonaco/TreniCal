package it.trenical.client.observer;

import it.trenical.common.Trip;

import java.util.Collection;

public interface TripsCache {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateTripsCache(Collection<Trip> cache);
    }
}
