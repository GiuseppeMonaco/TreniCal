package it.trenical.client.observer;

public class FidelityUserSubject extends AbstractSubject<FidelityUser.Observer> implements FidelityUser.Subject {
    @Override
    public void notifyObs() {
        for(FidelityUser.Observer obs : observers) obs.updateOnFidelityChange();
    }
}
