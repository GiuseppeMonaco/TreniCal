package it.trenical.client.observer;

public interface NotificationChange {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateOnNotificationChange();
    }
}
