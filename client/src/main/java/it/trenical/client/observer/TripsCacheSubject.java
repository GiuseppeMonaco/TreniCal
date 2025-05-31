package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class TripsCacheSubject extends AbstractSubject<TripsCache.Observer> implements TripsCache.Subject {
    @Override
    public void notifyObs() {
        for(TripsCache.Observer o : observers) o.updateTripsCache();
    }
}
