package it.trenical.client.observer;

public class PassengersNumberSubject extends AbstractSubject<PassengersNumber.Observer> implements PassengersNumber.Subject {
    @Override
    public void notifyObs() {
        for (PassengersNumber.Observer obs : observers) {
            obs.updateOnPassengersNumber();
        }
    }
}