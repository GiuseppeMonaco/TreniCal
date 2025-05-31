package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class CurrentTripSubject extends AbstractSubject<CurrentTrip.Observer> implements CurrentTrip.Subject {
    @Override
    public void notifyObs() {
        for (CurrentTrip.Observer obs : observers) {
            obs.updateOnCurrentTrip();
        }
    }
}
