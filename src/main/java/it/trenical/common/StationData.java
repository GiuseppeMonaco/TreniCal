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

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public void setProvince(String province) {
            this.province = province;
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
