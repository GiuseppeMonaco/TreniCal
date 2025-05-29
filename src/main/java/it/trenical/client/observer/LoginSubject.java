package it.trenical.client.observer;

public class LoginSubject extends AbstractSubject<Login.Observer> implements Login.Subject {
    @Override
    public void notifyObs() {
        for(Login.Observer obs : observers) obs.updateOnLogin();
    }
}
