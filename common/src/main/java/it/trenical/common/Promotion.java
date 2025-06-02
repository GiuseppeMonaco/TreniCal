package it.trenical.common;

public interface Promotion {

    String getCode();

    String getName();
    String getDescription();
    boolean isOnlyFidelityUser();
    float getDiscount();

    default int getDiscountPercentage() {
        int x = Math.round((1-getDiscount()) * 100);
        assert x >= 0 && x <= 100;
        return x;
    }
}
