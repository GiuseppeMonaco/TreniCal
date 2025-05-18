package it.trenical.common;

public class TrainData implements Train {

    private final int id;
    private final TrainType type;
    private final int economyCapacity;
    private final int businessCapacity;

    protected TrainData(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.economyCapacity = builder.economyCapacity;
        this.businessCapacity = builder.businessCapacity;
    }

    public static Builder newBuilder(int id) {
        return new Builder(id);
    }

    public static class Builder {
        private final int id;
        private TrainType type;
        private int economyCapacity;
        private int businessCapacity;

        protected Builder(int id) {
            this.id = id;
        }

        public Builder setType(TrainType type) {
            this.type = type;
            return this;
        }

        public Builder setEconomyCapacity(int economyCapacity) {
            this.economyCapacity = economyCapacity;
            return this;
        }

        public Builder setBusinessCapacity(int businessCapacity) {
            this.businessCapacity = businessCapacity;
            return this;
        }

        public TrainData build() {
            return new TrainData(this);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public TrainType getType() {
        return type;
    }

    @Override
    public int getEconomyCapacity() {
        return economyCapacity;
    }

    @Override
    public int getBusinessCapacity() {
        return businessCapacity;
    }
}
