package it.trenical.common;

import java.util.Calendar;

public interface Trip extends Data {

    Train getTrain();
    Calendar getDepartureTime();
    Route getRoute();

    int getAvailableEconomySeats();
    int getAvailableBusinessSeats();
}
