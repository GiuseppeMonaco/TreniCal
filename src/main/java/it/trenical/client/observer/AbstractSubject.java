package it.trenical.client.observer;

import java.util.ArrayList;
import java.util.Collection;

abstract class AbstractSubject<T> {

    protected final Collection<T> observers = new ArrayList<>();

    public void attach(T obs) {
        observers.add(obs);
    }

    public void detach(T obs) {
        observers.remove(obs);
    }

}
