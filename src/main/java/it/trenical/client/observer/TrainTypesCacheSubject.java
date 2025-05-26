package it.trenical.client.observer;

import it.trenical.common.TrainType;

import java.util.ArrayList;
import java.util.Collection;

public class TrainTypesCacheSubject implements TrainTypesCache.Subject {

    private final Collection<TrainTypesCache.Observer> observers = new ArrayList<>();
    private final Collection<TrainType> cache;

    public TrainTypesCacheSubject(Collection<TrainType> cache) {
        this.cache = cache;
    }

    @Override
    public void attach(TrainTypesCache.Observer obs) {
        observers.add(obs);
    }
    @Override
    public void detach(TrainTypesCache.Observer obs) {
        observers.remove(obs);
    }
    @Override
    public void notifyObs() {
        for(TrainTypesCache.Observer o : observers) o.updateTrainTypesCache(cache);
    }
}
