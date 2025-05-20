package it.trenical.common;

public class StationData implements Station {

    private final String name;
    private final String address;
    private final String town;
    private final String province;

    protected StationData(Builder builder) {
        this.name = builder.name;
        this.address = builder.address;
        this.town = builder.town;
        this.province = builder.province;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String address;
        private String town;
        private String province;

        protected Builder(){}

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setTown(String town) {
            this.town = town;
            return this;
        }

        public Builder setProvince(String province) {
            this.province = province;
            return this;
        }

        public StationData build() {
            return new StationData(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getTown() {
        return town;
    }

    @Override
    public String getProvince() {
        return province;
    }
}
