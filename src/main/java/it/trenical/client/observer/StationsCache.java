package it.trenical.client.observer;

import it.trenical.common.Station;

import java.util.Collection;

public interface StationsCache {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateStationsCache(Collection<Station> cache);
    }
}
