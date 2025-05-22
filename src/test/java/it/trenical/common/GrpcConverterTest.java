package it.trenical.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static it.trenical.common.GrpcConverter.*;

class GrpcConverterTest {

    static User user = new UserData("example@mail.com","pass123",true);
    static Promotion promotion = PromotionData.newBuilder("exampleCode")
            .setName("someName")
            .setDescription("someDescription")
            .setOnlyFidelityUser(true)
            .setDiscount(0.8f)
            .build();
    static TrainType trainType = new TrainTypeData("exampleType",32123);
    static Train train = TrainData.newBuilder(6)
            .setType(trainType)
            .setEconomyCapacity(16)
            .setBusinessCapacity(29)
            .build();
    static Station station = StationData.newBuilder("exampleStation")
            .setAddress("someAddress")
            .setProvince("someProvince")
            .setTown("someTown")
            .build();
    static Station station2 = StationData.newBuilder("exampleStation2")
            .setAddress("someAddress2")
            .setProvince("someProvince2")
            .setTown("someTown2")
            .build();
    static Route route = new RouteData(station, station2, 457);
    static Trip trip = TripData.newBuilder(route)
            .setTrain(train)
            .setDepartureTime(Calendar.getInstance())
            .setAvailableEconomySeats(11)
            .setAvailableBusinessSeats(19)
            .build();
    static Ticket ticket = TicketData.newBuilder(45, user)
            .setName("Mario")
            .setSurname("Rossi")
            .setPrice(102.53f)
            .setPromotion(promotion)
            .setTrip(trip)
            .setPaid(true)
            .build();

    private void assertPromotionEquals(Promotion expected, Promotion actual) {
        assertNotNull(expected, "Expected Promotion should not be null");
        assertNotNull(actual, "Actual Promotion should not be null");
        assertEquals(expected.getCode(), actual.getCode(), "Promotion code differs");
        assertEquals(expected.getName(), actual.getName(), "Promotion name differs");
        assertEquals(expected.getDescription(), actual.getDescription(), "Promotion description differs");
        assertEquals(expected.isOnlyFidelityUser(), actual.isOnlyFidelityUser(), "Promotion fidelity flag differs");
        assertEquals(expected.getDiscount(), actual.getDiscount(), 0.0001, "Promotion discount differs");
    }

    private void assertStationEquals(Station expected, Station actual) {
        assertNotNull(expected, "Expected Station should not be null");
        assertNotNull(actual, "Actual Station should not be null");
        assertEquals(expected.getName(), actual.getName(), "Station name differs");
        assertEquals(expected.getAddress(), actual.getAddress(), "Station address differs");
        assertEquals(expected.getTown(), actual.getTown(), "Station town differs");
        assertEquals(expected.getProvince(), actual.getProvince(), "Station province differs");
    }

    private void assertRouteEquals(Route expected, Route actual) {
        assertNotNull(expected, "Expected Route should not be null");
        assertNotNull(actual, "Actual Route should not be null");
        assertStationEquals(expected.getDepartureStation(), actual.getDepartureStation());
        assertStationEquals(expected.getArrivalStation(), actual.getArrivalStation());
        assertEquals(expected.getDistance(), actual.getDistance(), "Route distance differs");
    }

    private void assertTrainTypeEquals(TrainType expected, TrainType actual) {
        assertNotNull(expected, "Expected TrainType should not be null");
        assertNotNull(actual, "Actual TrainType should not be null");
        assertEquals(expected.getName(), actual.getName(), "TrainType name differs");
        assertEquals(expected.getPrice(), actual.getPrice(), 0.0001, "TrainType price differs");
    }

    private void assertTrainEquals(Train expected, Train actual) {
        assertNotNull(expected, "Expected Train should not be null");
        assertNotNull(actual, "Actual Train should not be null");
        assertEquals(expected.getId(), actual.getId(), "Train ID differs");
        assertTrainTypeEquals(expected.getType(), actual.getType());
        assertEquals(expected.getEconomyCapacity(), actual.getEconomyCapacity(), "Economy capacity differs");
        assertEquals(expected.getBusinessCapacity(), actual.getBusinessCapacity(), "Business capacity differs");
    }

    private void assertUserEquals(User expected, User actual) {
        assertNotNull(expected, "Expected User should not be null");
        assertNotNull(actual, "Actual User should not be null");
        assertEquals(expected.getEmail(), actual.getEmail(), "User email differs");
        assertEquals(expected.getPassword(), actual.getPassword(), "User password differs");
        assertEquals(expected.isFidelity(), actual.isFidelity(), "User fidelity flag differs");
    }

    private void assertTripEquals(Trip expected, Trip actual) {
        assertNotNull(expected, "Expected Trip should not be null");
        assertNotNull(actual, "Actual Trip should not be null");
        assertTrainEquals(expected.getTrain(), actual.getTrain());
        Calendar expTime = expected.getDepartureTime();
        Calendar actTime = actual.getDepartureTime();
        assertNotNull(expTime, "Expected departure time should not be null");
        assertNotNull(actTime, "Actual departure time should not be null");
        assertEquals(expTime.getTimeInMillis(), actTime.getTimeInMillis(), "Departure time differs");
        assertRouteEquals(expected.getRoute(), actual.getRoute());
        assertEquals(expected.getAvailableEconomySeats(), actual.getAvailableEconomySeats(), "Available economy seats differs");
        assertEquals(expected.getAvailableBusinessSeats(), actual.getAvailableBusinessSeats(), "Available business seats differs");
    }

    private void assertTicketEquals(Ticket expected, Ticket actual) {
        assertNotNull(expected, "Expected Ticket should not be null");
        assertNotNull(actual, "Actual Ticket should not be null");
        assertEquals(expected.getId(), actual.getId(), "Ticket ID differs");
        assertUserEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getName(), actual.getName(), "Ticket holder name differs");
        assertEquals(expected.getSurname(), actual.getSurname(), "Ticket holder surname differs");
        assertEquals(expected.getPrice(), actual.getPrice(), 0.0001, "Ticket price differs");
        assertPromotionEquals(expected.getPromotion(), actual.getPromotion());
        assertTripEquals(expected.getTrip(), actual.getTrip());
        assertEquals(expected.isPaid(), actual.isPaid(), "Ticket paid flag differs");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void convertUser() {
        assertUserEquals(user,convert(convert(user)));
    }

    @Test
    void convertPromotion() {
        assertPromotionEquals(promotion,convert(convert(promotion)));
    }

    @Test
    void convertTicket() {
        assertTicketEquals(ticket,convert(convert(ticket)));
    }

    @Test
    void convertTrip() {
        assertTripEquals(trip,convert(convert(trip)));
    }

    @Test
    void convertTrain() {
        assertTrainEquals(train,convert(convert(train)));
    }

    @Test
    void convertStation() {
        assertStationEquals(station,convert(convert(station)));
        assertStationEquals(station2,convert(convert(station2)));
    }

    @Test
    void convertRoute() {
        assertRouteEquals(route,convert(convert(route)));
    }

    @Test
    void convertTrainType() {
        assertTrainTypeEquals(trainType,convert(convert(trainType)));
    }

}