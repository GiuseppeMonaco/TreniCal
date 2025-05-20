package it.trenical.common;

public class TicketData implements Ticket {

    private final int id;
    private final User user;
    private final String name;
    private final String surname;
    private final float price;
    private final Trip trip;
    private final Promotion promotion;
    private final boolean isPaid;

    protected TicketData(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.name = builder.name;
        this.surname = builder.surname;
        this.price = builder.price;
        this.trip = builder.trip;
        this.promotion = builder.promotion;
        this.isPaid = builder.isPaid;
    }

    public static Builder newBuilder(int id, User user) {
        return new Builder(id, user);
    }

    public static class Builder {
        private final int id;
        private final User user;
        private String name;
        private String surname;
        private float price;
        private Trip trip;
        private Promotion promotion;
        private boolean isPaid;

        protected Builder(int id, User user) {
            this.id = id;
            this.user = user;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder setPrice(float price) {
            this.price = price;
            return this;
        }

        public Builder setTrip(Trip trip) {
            this.trip = trip;
            return this;
        }

        public Builder setPromotion(Promotion promotion) {
            this.promotion = promotion;
            return this;
        }

        public Builder setPaid(boolean paid) {
            isPaid = paid;
            return this;
        }

        public TicketData build() {
            return new TicketData(this);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public Promotion getPromotion() {
        return promotion;
    }

    @Override
    public Trip getTrip() {
        return trip;
    }

    @Override
    public boolean isPaid() {
        return isPaid;
    }

}
