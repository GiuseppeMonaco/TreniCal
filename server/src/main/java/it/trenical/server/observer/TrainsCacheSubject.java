package it.trenical.server.observer;

import it.trenical.common.observer.AbstractSubject;

public class TrainsCacheSubject extends AbstractSubject<TrainsCache.Observer> implements TrainsCache.Subject {
    @Override
    public void notifyObs() {
        for (TrainsCache.Observer o : observers) o.updateTrainsCache();
    }
}

