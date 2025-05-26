package it.trenical.client.observer;

import it.trenical.common.Ticket;

import java.util.Collection;

public interface TicketsCache {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateTicketsCache(Collection<Ticket> cache);
    }
}
