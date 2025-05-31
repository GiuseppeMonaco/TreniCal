package it.trenical.client.observer;

public interface TicketsCache {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateTicketsCache();
    }
}
