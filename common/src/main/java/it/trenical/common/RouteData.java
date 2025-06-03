package it.trenical.common;

import java.io.Serial;
import java.io.Serializable;

public class RouteData implements Route, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteData routeData)) return false;
        return departureStation.equals(routeData.departureStation) && arrivalStation.equals(routeData.arrivalStation);
    }

    @Override
    public int hashCode() {
        int result = departureStation.hashCode();
        result = 31 * result + arrivalStation.hashCode();
        return result;
    }
}
