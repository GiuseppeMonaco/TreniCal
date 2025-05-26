package it.trenical.client.observer;

import it.trenical.common.TrainType;

import java.util.Collection;

public interface TrainTypesCache {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateTrainTypesCache(Collection<TrainType> cache);
    }
}
