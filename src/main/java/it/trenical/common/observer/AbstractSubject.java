package it.trenical.common.observer;

import java.util.Collection;
import java.util.LinkedList;

public abstract class AbstractSubject<T> {

    protected final Collection<T> observers = new LinkedList<>();

    public void attach(T obs) {
        observers.add(obs);
    }

    public void detach(T obs) {
        observers.remove(obs);
    }

}
