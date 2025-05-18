package it.trenical.common;

public interface Ticket extends Data {

    int getId();
    User getUser();

    String getName();
    String getSurname();
    float getPrice();
    Promotion getPromotion();
    Trip getTrip();
    boolean isPaid();

}
