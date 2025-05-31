package it.trenical.common;

public class TrainTypeData implements TrainType {

    private final String name;
    private final float price;

    public TrainTypeData(String name, float price) {
        if(name == null) throw new IllegalArgumentException("name cannot be null");
        this.name = name.trim();
        this.price = price;
    }

    public TrainTypeData(String name) {
        this(name, 0);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainTypeData that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
