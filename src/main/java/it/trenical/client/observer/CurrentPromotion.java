package it.trenical.client.observer;

public interface CurrentPromotion {

    interface Subject {
        void attach(Observer obs);
        void detach(Observer obs);
        void notifyObs();
    }

    interface Observer {
        void updateOnCurrentPromotion();
    }
}

