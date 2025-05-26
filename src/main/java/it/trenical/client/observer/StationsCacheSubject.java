package it.trenical.client.observer;

import it.trenical.common.Station;

import java.util.ArrayList;
import java.util.Collection;

public class StationsCacheSubject implements StationsCache.Subject {

    private final Collection<StationsCache.Observer> observers = new ArrayList<>();
    private final Collection<Station> cache;

    public StationsCacheSubject(Collection<Station> cache) {
        this.cache = cache;
    }

    @Override
    public void attach(StationsCache.Observer obs) {
        observers.add(obs);
    }
    @Override
    public void detach(StationsCache.Observer obs) {
        observers.remove(obs);
    }
    @Override
    public void notifyObs() {
        for(StationsCache.Observer o : observers) o.updateStationsCache(cache);
    }

}
