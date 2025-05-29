package it.trenical.client.observer;

public class TicketsCacheSubject extends AbstractSubject<TicketsCache.Observer> implements TicketsCache.Subject {
    @Override
    public void notifyObs() {
        for(TicketsCache.Observer o : observers) o.updateTicketsCache();
    }
}
