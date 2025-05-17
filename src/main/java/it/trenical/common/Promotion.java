package it.trenical.common;

public interface Promotion {

    String getCode();

    String getName();
    String getDescription();
    boolean isOnlyFidelityUser();
    float getDiscount();

}
