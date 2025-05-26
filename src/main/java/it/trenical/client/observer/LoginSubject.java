package it.trenical.client.observer;

import java.util.ArrayList;
import java.util.Collection;

public class LoginSubject implements Login.Subject {

    private final Collection<Login.Observer> observers = new ArrayList<>();
    @Override
    public void attach(Login.Observer obs) {
        observers.add(obs);
    }
    @Override
    public void detach(Login.Observer obs) {
        observers.remove(obs);
    }
    @Override
    public void notifyObs() {
        for(Login.Observer obs : observers) obs.updateOnLogin();
    }

}
