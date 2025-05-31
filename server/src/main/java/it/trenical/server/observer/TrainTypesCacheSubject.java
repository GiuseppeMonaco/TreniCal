package it.trenical.server.observer;

import it.trenical.common.observer.AbstractSubject;

public class TrainTypesCacheSubject extends AbstractSubject<TrainTypesCache.Observer> implements TrainTypesCache.Subject {
    @Override
    public void notifyObs() {
        for(TrainTypesCache.Observer o : observers) o.updateTrainTypesCache();
    }
}
