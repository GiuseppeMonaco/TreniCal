package it.trenical.common;

import java.util.Calendar;

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

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Train train;
        private Route route;
        private Calendar departureTime;
        private int availableEconomySeats;
        private int availableBusinessSeats;

        private Builder(){}

        public Builder setTrain(Train train) {
            this.train = train;
            return this;
        }

        public Builder setRoute(Route route) {
            this.route = route;
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
}
