package it.trenical.common.testUtil;

import it.trenical.common.*;

import java.util.Calendar;

public class DataSamples {

    static public final User user = new UserData("example@mail.com","pass123",true);
    static public final Promotion promotion = PromotionData.newBuilder("exampleCode")
            .setName("someName")
            .setDescription("someDescription")
            .setOnlyFidelityUser(true)
            .setDiscount(0.8f)
            .build();
    static public final TrainType trainType = new TrainTypeData("exampleType",32123);
    static public final Train train = TrainData.newBuilder(6)
            .setType(trainType)
            .setEconomyCapacity(16)
            .setBusinessCapacity(29)
            .build();
    static public final Station station = StationData.newBuilder("exampleStation")
            .setAddress("someAddress")
            .setProvince("someProvince")
            .setTown("someTown")
            .build();
    static public final Station station2 = StationData.newBuilder("exampleStation2")
            .setAddress("someAddress2")
            .setProvince("someProvince2")
            .setTown("someTown2")
            .build();
    static public final Route route = new RouteData(station, station2, 457);
    static public final Trip trip = TripData.newBuilder(route)
            .setTrain(train)
            .setDepartureTime(Calendar.getInstance())
            .setAvailableEconomySeats(11)
            .setAvailableBusinessSeats(19)
            .build();
    static public final Ticket ticket = TicketData.newBuilder(45)
            .setUser(user)
            .setName("Mario")
            .setSurname("Rossi")
            .setPrice(102.53f)
            .setPromotion(promotion)
            .setTrip(trip)
            .setPaid(true)
            .setBusiness(true)
            .build();

    static public final User nullUser = new UserData("example@mail.com");
    static public final Promotion nullPromotion = PromotionData.newBuilder("exampleCode").build();
    static public final TrainType nullTrainType = new TrainTypeData("exampleType");
    static public final Train nullTrain = TrainData.newBuilder(6).build();
    static public final Station nullStation = StationData.newBuilder("exampleStation").build();
    static public final Station nullStation2 = StationData.newBuilder("exampleStation2").build();
    static public final Route nullRoute = new RouteData(nullStation, nullStation2);
    static public final Trip nullTrip = TripData.newBuilder(nullRoute).build();
    static public final Ticket nullTicket = TicketData.newBuilder(45).build();

}
