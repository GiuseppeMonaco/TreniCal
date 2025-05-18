package it.trenical.common;

public class RouteData implements Route {

    private final Station departureStation;
    private final Station arrivalStation;
    private final int distance;

    public RouteData(Station departureStation, Station arrivalStation, int distance) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.distance = distance;
    }

    @Override
    public Station getDepartureStation() {
        return departureStation;
    }

    @Override
    public Station getArrivalStation() {
        return arrivalStation;
    }

    @Override
    public int getDistance() {
        return distance;
    }
}
