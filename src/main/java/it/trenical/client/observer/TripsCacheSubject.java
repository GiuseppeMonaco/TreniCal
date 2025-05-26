package it.trenical.client.observer;

import it.trenical.common.Trip;

import java.util.ArrayList;
import java.util.Collection;

public class TripsCacheSubject implements TripsCache.Subject {

    private final Collection<TripsCache.Observer> observers = new ArrayList<>();
    private final Collection<Trip> cache;

    public TripsCacheSubject(Collection<Trip> cache) {
        this.cache = cache;
    }

    @Override
    public void attach(TripsCache.Observer obs) {
        observers.add(obs);
    }
    @Override
    public void detach(TripsCache.Observer obs) {
        observers.remove(obs);
    }
    @Override
    public void notifyObs() {
        for(TripsCache.Observer o : observers) o.updateTripsCache(cache);
    }
}
