package it.trenical.client.observer;

public class CurrentTripSubject extends AbstractSubject<CurrentTrip.Observer> implements CurrentTrip.Subject {
    @Override
    public void notifyObs() {
        for (CurrentTrip.Observer obs : observers) {
            obs.updateOnCurrentTrip();
        }
    }
}
