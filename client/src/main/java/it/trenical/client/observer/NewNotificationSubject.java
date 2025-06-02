package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class NewNotificationSubject extends AbstractSubject<NewNotification.Observer> implements NewNotification.Subject {
    @Override
    public void notifyObs() {
        for (NewNotification.Observer obs : observers) {
            obs.updateOnNewNotification();
        }
    }
}
