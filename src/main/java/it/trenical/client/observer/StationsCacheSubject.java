package it.trenical.client.observer;

public class StationsCacheSubject extends AbstractSubject<StationsCache.Observer> implements StationsCache.Subject {
    @Override
    public void notifyObs() {
        for(StationsCache.Observer o : observers) o.updateStationsCache();
    }
}
