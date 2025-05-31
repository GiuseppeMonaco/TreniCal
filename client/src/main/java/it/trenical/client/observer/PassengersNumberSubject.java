package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class PassengersNumberSubject extends AbstractSubject<PassengersNumber.Observer> implements PassengersNumber.Subject {
    @Override
    public void notifyObs() {
        for (PassengersNumber.Observer obs : observers) {
            obs.updateOnPassengersNumber();
        }
    }
}