package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class CurrentPriceSubject extends AbstractSubject<CurrentPrice.Observer> implements CurrentPrice.Subject {
    @Override
    public void notifyObs() {
        for (CurrentPrice.Observer obs : observers) {
            obs.updateOnCurrentPrice();
        }
    }
}
