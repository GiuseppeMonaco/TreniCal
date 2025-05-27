package it.trenical.client.observer;

import it.trenical.common.User;

import java.util.ArrayList;
import java.util.Collection;

public class FidelityUserSubject implements FidelityUser.Subject {

    private final Collection<FidelityUser.Observer> observers = new ArrayList<>();
    private final User currentUser;

    public FidelityUserSubject(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void attach(FidelityUser.Observer obs) {
        observers.add(obs);
    }
    @Override
    public void detach(FidelityUser.Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObs() {
        for(FidelityUser.Observer obs : observers) obs.updateOnFidelityChange(currentUser);
    }
}
