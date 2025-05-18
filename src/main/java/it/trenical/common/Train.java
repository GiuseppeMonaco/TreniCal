package it.trenical.common;

public interface Train extends Data {

    int getId();

    TrainType getType();
    int getEconomyCapacity();
    int getBusinessCapacity();

}
