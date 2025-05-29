package it.trenical.client.observer;

public class CurrentPriceSubject extends AbstractSubject<CurrentPrice.Observer> implements CurrentPrice.Subject {
    @Override
    public void notifyObs() {
        for (CurrentPrice.Observer obs : observers) {
            obs.updateOnCurrentPrice();
        }
    }
}
