package it.trenical.common;

public class TrainTypeData implements TrainType {

    private final String name;
    private final float price;

    public TrainTypeData(String name, float price) {
        if(name == null) throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getPrice() {
        return price;
    }
}
