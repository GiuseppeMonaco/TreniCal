package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class CurrentPromotionSubject extends AbstractSubject<CurrentPromotion.Observer> implements CurrentPromotion.Subject {
    @Override
    public void notifyObs() {
        for (CurrentPromotion.Observer obs : observers) {
            obs.updateOnCurrentPromotion();
        }
    }
}

