package it.trenical.server.observer;

public interface TrainsCache {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateTrainsCache();
    }
}
