package it.trenical.common;

public interface Ticket {

    int getId();

    User getUser();
    String getName();
    String getSurname();
    float getPrice();
    Promotion getPromotion();
    Trip getTrip();
    boolean isPaid();
    boolean isBusiness();

    default float calculatePrice(float distanceMult, float businessMult) {
        float ret = 0f;

        Trip t = getTrip();
        if (t != null) {
            if (t.getRoute() != null) ret += t.getRoute().getDistance() * distanceMult;

            Train tr = t.getTrain();
            if (tr != null && tr.getType() != null)  ret *= tr.getType().getPrice();
        }

        if (isBusiness()) ret *= businessMult;

        Promotion p = getPromotion();
        if (p != null) ret *= p.getDiscount();

        return ret;
    }

}
