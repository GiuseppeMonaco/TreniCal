package it.trenical.server.observer;

import it.trenical.common.observer.AbstractSubject;

public class TicketsCacheSubject extends AbstractSubject<TicketsCache.Observer> implements TicketsCache.Subject {
    @Override
    public void notifyObs() {
        for(TicketsCache.Observer o : observers) o.updateTicketsCache();
    }
}
