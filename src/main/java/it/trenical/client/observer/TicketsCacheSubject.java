package it.trenical.client.observer;

import it.trenical.common.Ticket;

import java.util.ArrayList;
import java.util.Collection;

public class TicketsCacheSubject implements TicketsCache.Subject {

    private final Collection<TicketsCache.Observer> observers = new ArrayList<>();
    private final Collection<Ticket> cache;

    public TicketsCacheSubject(Collection<Ticket> cache) {
        this.cache = cache;
    }

    @Override
    public void attach(TicketsCache.Observer obs) {
        observers.add(obs);
    }
    @Override
    public void detach(TicketsCache.Observer obs) {
        observers.remove(obs);
    }
    @Override
    public void notifyObs() {
        for(TicketsCache.Observer o : observers) o.updateTicketsCache(cache);
    }

}
