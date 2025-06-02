package it.trenical.client.observer;

import it.trenical.common.observer.AbstractSubject;

public class NotificationChangeSubject extends AbstractSubject<NotificationChange.Observer> implements NotificationChange.Subject {
    @Override
    public void notifyObs() {
        for (NotificationChange.Observer obs : observers) {
            obs.updateOnNotificationChange();
        }
    }
}
