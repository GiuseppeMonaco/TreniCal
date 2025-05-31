package it.trenical.common;

public interface Route {

    Station getDepartureStation();
    Station getArrivalStation();

    int getDistance();

}
