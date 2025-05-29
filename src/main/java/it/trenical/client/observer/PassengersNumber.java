package it.trenical.client.observer;

public interface PassengersNumber {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateOnPassengersNumber();
    }
}

