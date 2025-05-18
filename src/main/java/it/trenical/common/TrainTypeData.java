package it.trenical.common;

public class TrainTypeData implements TrainType {

    private final String name;
    private final float price;

    public TrainTypeData(String name, float price) {
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
