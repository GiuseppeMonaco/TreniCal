package it.trenical.client.observer;

public class LogoutSubject extends AbstractSubject<Logout.Observer> implements Logout.Subject {
    @Override
    public void notifyObs() {
        for(Logout.Observer o : observers) o.updateOnLogout();
    }
}
