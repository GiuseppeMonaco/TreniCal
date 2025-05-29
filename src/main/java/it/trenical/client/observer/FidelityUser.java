package it.trenical.client.observer;

public interface FidelityUser {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateOnFidelityChange();
    }
}
