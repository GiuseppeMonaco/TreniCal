package it.trenical.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataEquals {

    static public void assertPromotionEquals(Promotion expected, Promotion actual) {
        if(expected == actual) return;
        assertNotNull(expected, "Expected Promotion should not be null");
        assertNotNull(actual, "Actual Promotion should not be null");
        assertEquals(expected.getCode(), actual.getCode(), "Promotion code differs");
        assertEquals(expected.getName(), actual.getName(), "Promotion name differs");
        assertEquals(expected.getDescription(), actual.getDescription(), "Promotion description differs");
        assertEquals(expected.isOnlyFidelityUser(), actual.isOnlyFidelityUser(), "Promotion fidelity flag differs");
        assertEquals(expected.getDiscount(), actual.getDiscount(), 0.0001, "Promotion discount differs");
    }

    static public void assertStationEquals(Station expected, Station actual) {
        if(expected == actual) return;
        assertNotNull(expected, "Expected Station should not be null");
        assertNotNull(actual, "Actual Station should not be null");
        assertEquals(expected.getName(), actual.getName(), "Station name differs");
        assertEquals(expected.getAddress(), actual.getAddress(), "Station address differs");
        assertEquals(expected.getTown(), actual.getTown(), "Station town differs");
        assertEquals(expected.getProvince(), actual.getProvince(), "Station province differs");
    }

    static public void assertRouteEquals(Route expected, Route actual) {
        if(expected == actual) return;
        assertNotNull(expected, "Expected Route should not be null");
        assertNotNull(actual, "Actual Route should not be null");
        assertStationEquals(expected.getDepartureStation(), actual.getDepartureStation());
        assertStationEquals(expected.getArrivalStation(), actual.getArrivalStation());
        assertEquals(expected.getDistance(), actual.getDistance(), "Route distance differs");
    }

    static public void assertTrainTypeEquals(TrainType expected, TrainType actual) {
        if(expected == actual) return;
        assertNotNull(expected, "Expected TrainType should not be null");
        assertNotNull(actual, "Actual TrainType should not be null");
        assertEquals(expected.getName(), actual.getName(), "TrainType name differs");
        assertEquals(expected.getPrice(), actual.getPrice(), 0.0001, "TrainType price differs");
    }

    static public void assertTrainEquals(Train expected, Train actual) {
        if(expected == actual) return;
        assertNotNull(expected, "Expected Train should not be null");
        assertNotNull(actual, "Actual Train should not be null");
        assertEquals(expected.getId(), actual.getId(), "Train ID differs");
        assertTrainTypeEquals(expected.getType(), actual.getType());
        assertEquals(expected.getEconomyCapacity(), actual.getEconomyCapacity(), "Economy capacity differs");
        assertEquals(expected.getBusinessCapacity(), actual.getBusinessCapacity(), "Business capacity differs");
    }

    static public void assertUserEquals(User expected, User actual) {
        if(expected == actual) return;
        assertNotNull(expected, "Expected User should not be null");
        assertNotNull(actual, "Actual User should not be null");
        assertEquals(expected.getEmail(), actual.getEmail(), "User email differs");
        assertEquals(expected.getPassword(), actual.getPassword(), "User password differs");
        assertEquals(expected.isFidelity(), actual.isFidelity(), "User fidelity flag differs");
    }

    static public void assertTripEquals(Trip expected, Trip actual) {
        if(expected == actual) return;
        assertNotNull(expected, "Expected Trip should not be null");
        assertNotNull(actual, "Actual Trip should not be null");
        assertTrainEquals(expected.getTrain(), actual.getTrain());
        assertEquals(expected.getDepartureTime(),actual.getDepartureTime(), "Departure time differs");
        assertRouteEquals(expected.getRoute(), actual.getRoute());
        assertEquals(expected.getAvailableEconomySeats(), actual.getAvailableEconomySeats(), "Available economy seats differs");
        assertEquals(expected.getAvailableBusinessSeats(), actual.getAvailableBusinessSeats(), "Available business seats differs");
    }

    static public void assertTicketEquals(Ticket expected, Ticket actual) {
        if(expected == actual) return;
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
}
