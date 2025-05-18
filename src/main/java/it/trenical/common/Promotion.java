package it.trenical.common;

public interface Promotion extends Data {

    String getCode();

    String getName();
    String getDescription();
    boolean isOnlyFidelityUser();
    float getDiscount();

}
