package it.trenical.common;

public interface Ticket {

    float PRICE_MULTIPLIER_DISTANCE = 0.05f; // TODO inserire questi moltiplicatori in un file di configurazione
    float PRICE_MULTIPLIER_BUSINESS = 1.3f;

    int getId();

    User getUser();
    String getName();
    String getSurname();
    float getPrice();
    Promotion getPromotion();
    Trip getTrip();
    boolean isPaid();
    boolean isBusiness();

    default float calculatePrice() {
        float ret = 0f;

        Trip t = getTrip();
        if (t != null) {
            if (t.getRoute() != null) ret += getTrip().getRoute().getDistance() * PRICE_MULTIPLIER_DISTANCE;

            Train tr = t.getTrain();
            if (tr != null && tr.getType() != null)  ret *= tr.getType().getPrice();
        }

        if (isBusiness()) ret *= PRICE_MULTIPLIER_BUSINESS;

        Promotion p = getPromotion();
        if (p != null) ret *= p.getDiscount();

        return ret;
    }

}
