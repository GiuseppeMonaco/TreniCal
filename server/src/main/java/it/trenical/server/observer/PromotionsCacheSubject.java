package it.trenical.server.observer;

import it.trenical.common.observer.AbstractSubject;

public class PromotionsCacheSubject extends AbstractSubject<PromotionsCache.Observer> implements PromotionsCache.Subject {
    @Override
    public void notifyObs() {
        for (PromotionsCache.Observer o : observers) o.updatePromotionsCache();
    }
}

