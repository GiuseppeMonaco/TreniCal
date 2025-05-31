package it.trenical.server.observer;

public interface RoutesCache {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateRoutesCache();
    }
}

