package it.trenical.client.observer;

import java.util.ArrayList;
import java.util.Collection;

public class LogoutSubject implements Logout.Subject {

    private final Collection<Logout.Observer> observers = new ArrayList<>();
    @Override
    public void attach(Logout.Observer obs) {
        observers.add(obs);
    }
    @Override
    public void detach(Logout.Observer obs) {
        observers.remove(obs);
    }
    @Override
    public void notifyObs() {
        for(Logout.Observer o : observers) o.updateOnLogout();
    }

}
