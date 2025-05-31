package it.trenical.common;

public class StationData implements Station {

    private final String name;
    private final String address;
    private final String town;
    private final String province;

    private StationData(Builder builder) {
        this.name = builder.name;
        this.address = builder.address;
        this.town = builder.town;
        this.province = builder.province;
    }

    public static Builder newBuilder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private String address;
        private String town;
        private String province;

        private Builder(String name){
            if(name == null) throw new IllegalArgumentException("name cannot be null");
            this.name = name.trim();
        }

        public Builder setAddress(String address) {
            this.address = address.trim();
            return this;
        }

        public Builder setTown(String town) {
            this.town = town.trim();
            return this;
        }

        public Builder setProvince(String province) {
            this.province = province.trim();
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationData that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
