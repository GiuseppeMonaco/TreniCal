package it.trenical.client.observer;

public class TripsCacheSubject extends AbstractSubject<TripsCache.Observer> implements TripsCache.Subject {
    @Override
    public void notifyObs() {
        for(TripsCache.Observer o : observers) o.updateTripsCache();
    }
}
