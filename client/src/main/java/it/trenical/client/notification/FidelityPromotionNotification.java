package it.trenical.client.notification;

import it.trenical.common.Promotion;

public class FidelityPromotionNotification extends Notification {

    private final Promotion newPromotion;

    public FidelityPromotionNotification(long timestamp, Promotion newPromotion) {
        super(timestamp);
        if(newPromotion == null) throw new IllegalArgumentException("promotion cannot be null");
        this.newPromotion = newPromotion;
    }

    public Promotion getNewPromotion() {
        return newPromotion;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FidelityPromotionNotification that)) return false;
        return newPromotion.equals(that.newPromotion);
    }

    @Override
    public int hashCode() {
        return newPromotion.hashCode();
    }
}
