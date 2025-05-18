package it.trenical.common;

public interface Route extends Data {

    Station getDepartureStation();
    Station getArrivalStation();

    int getDistance();

}
