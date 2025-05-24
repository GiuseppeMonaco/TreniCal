package it.trenical.common;

import org.junit.jupiter.api.Test;

import static it.trenical.common.DataEquals.*;
import static it.trenical.common.DataSamples.*;
import static it.trenical.common.GrpcConverter.*;

class GrpcConverterTest {

    @Test
    void convertUser() {
        assertUserEquals(user,convert(convert(user)));
        assertUserEquals(nullUser,convert(convert(nullUser)));
    }

    @Test
    void convertPromotion() {
        assertPromotionEquals(promotion,convert(convert(promotion)));
        assertPromotionEquals(nullPromotion,convert(convert(nullPromotion)));
    }

    @Test
    void convertTicket() {
        assertTicketEquals(ticket,convert(convert(ticket)));
        assertTicketEquals(nullTicket,convert(convert(nullTicket)));
    }

    @Test
    void convertTrip() {
        assertTripEquals(trip,convert(convert(trip)));
        assertTripEquals(nullTrip,convert(convert(nullTrip)));
    }

    @Test
    void convertTrain() {
        assertTrainEquals(train,convert(convert(train)));
        assertTrainEquals(nullTrain,convert(convert(nullTrain)));
    }

    @Test
    void convertStation() {
        assertStationEquals(station,convert(convert(station)));
        assertStationEquals(station2,convert(convert(station2)));
        assertStationEquals(nullStation,convert(convert(nullStation)));
        assertStationEquals(nullStation2,convert(convert(nullStation2)));
    }

    @Test
    void convertRoute() {
        assertRouteEquals(route,convert(convert(route)));
        assertRouteEquals(nullRoute,convert(convert(nullRoute)));
    }

    @Test
    void convertTrainType() {
        assertTrainTypeEquals(trainType,convert(convert(trainType)));
        assertTrainTypeEquals(nullTrainType,convert(convert(nullTrainType)));
    }

}