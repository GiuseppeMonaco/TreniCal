package it.trenical.common;

import java.util.Calendar;
import java.util.Objects;

public class TripData implements Trip {

    private final Train train;
    private final Route route;
    private final Calendar departureTime;
    private final int availableEconomySeats;
    private final int availableBusinessSeats;

    private TripData(Builder builder) {
        this.train = builder.train;
        this.route = builder.route;
        this.departureTime = builder.departureTime;
        this.availableEconomySeats = builder.availableEconomySeats;
        this.availableBusinessSeats = builder.availableBusinessSeats;
    }

    public static Builder newBuilder(Route route) {
        return new Builder(route);
    }

    public static class Builder {
        private Train train;
        private final Route route;
        private Calendar departureTime;
        private int availableEconomySeats;
        private int availableBusinessSeats;

        private Builder(Route route){
            if(route == null) throw new IllegalArgumentException("route cannot be null");
            this.route = route;
        }

        public Builder setTrain(Train train) {
            this.train = train;
            return this;
        }

        public Builder setDepartureTime(Calendar departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public Builder setAvailableEconomySeats(int availableEconomySeats) {
            this.availableEconomySeats = availableEconomySeats;
            return this;
        }

        public Builder setAvailableBusinessSeats(int availableBusinessSeats) {
            this.availableBusinessSeats = availableBusinessSeats;
            return this;
        }

        public TripData build() {
            return new TripData(this);
        }
    }

    @Override
    public Train getTrain() {
        return train;
    }

    @Override
    public Calendar getDepartureTime() {
        return departureTime;
    }

    @Override
    public Route getRoute() {
        return route;
    }

    @Override
    public int getAvailableEconomySeats() {
        return availableEconomySeats;
    }

    @Override
    public int getAvailableBusinessSeats() {
        return availableBusinessSeats;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripData tripData)) return false;
        return Objects.equals(train, tripData.train) && Objects.equals(route, tripData.route) && Objects.equals(departureTime, tripData.departureTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(train);
        result = 31 * result + Objects.hashCode(route);
        result = 31 * result + Objects.hashCode(departureTime);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s - on %s",
                getRoute().getDepartureStation().getTown(),
                getRoute().getArrivalStation().getTown(),
                dateFormatter.format(getDepartureTime().getTime())
        );
    }
}
