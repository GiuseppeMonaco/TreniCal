package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class FidelityUserSubject extends AbstractSubject<FidelityUser.Observer> implements FidelityUser.Subject {
    @Override
    public void notifyObs() {
        for(FidelityUser.Observer obs : observers) obs.updateOnFidelityChange();
    }
}
