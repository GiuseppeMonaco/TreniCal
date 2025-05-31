package it.trenical.server.observer;

import it.trenical.common.observer.AbstractSubject;

public class UsersCacheSubject extends AbstractSubject<UsersCache.Observer> implements UsersCache.Subject {
    @Override
    public void notifyObs() {
        for (UsersCache.Observer o : observers) o.updateUsersCache();
    }
}

