package it.trenical.common;

public class RouteData implements Route {

    private final Station departureStation;
    private final Station arrivalStation;
    private final int distance;

    public RouteData(Station departureStation, Station arrivalStation, int distance) {
        if(departureStation == null) throw new IllegalArgumentException("departureStation cannot be null");
        if(arrivalStation == null) throw new IllegalArgumentException("arrivalStation cannot be null");
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.distance = distance;
    }

    public RouteData(Station departureStation, Station arrivalStation) {
        this(departureStation,arrivalStation,0);
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
