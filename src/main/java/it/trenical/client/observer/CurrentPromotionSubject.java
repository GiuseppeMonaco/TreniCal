package it.trenical.client.observer;

public class CurrentPromotionSubject extends AbstractSubject<CurrentPromotion.Observer> implements CurrentPromotion.Subject {
    @Override
    public void notifyObs() {
        for (CurrentPromotion.Observer obs : observers) {
            obs.updateOnCurrentPromotion();
        }
    }
}

