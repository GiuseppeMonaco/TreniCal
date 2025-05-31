package it.trenical.server.observer;

import it.trenical.common.observer.AbstractSubject;

public class RoutesCacheSubject extends AbstractSubject<RoutesCache.Observer> implements RoutesCache.Subject {
    @Override
    public void notifyObs() {
        for (RoutesCache.Observer o : observers) o.updateRoutesCache();
    }
}
