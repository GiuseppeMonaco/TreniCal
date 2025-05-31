package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class LogoutSubject extends AbstractSubject<Logout.Observer> implements Logout.Subject {
    @Override
    public void notifyObs() {
        for(Logout.Observer o : observers) o.updateOnLogout();
    }
}
