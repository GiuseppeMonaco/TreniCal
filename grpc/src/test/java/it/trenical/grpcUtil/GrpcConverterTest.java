package it.trenical.grpcUtil;

import it.trenical.common.testUtil.DataEquals;
import it.trenical.common.testUtil.DataSamples;

import org.junit.jupiter.api.Test;

import static it.trenical.grpcUtil.GrpcConverter.convert;

class GrpcConverterTest {

    @Test
    void convertUser() {
        DataEquals.assertUserEquals(DataSamples.user,convert(convert(DataSamples.user)));
        DataEquals.assertUserEquals(DataSamples.nullUser,convert(convert(DataSamples.nullUser)));
    }

    @Test
    void convertPromotion() {
        DataEquals.assertPromotionEquals(DataSamples.promotion,convert(convert(DataSamples.promotion)));
        DataEquals.assertPromotionEquals(DataSamples.nullPromotion,convert(convert(DataSamples.nullPromotion)));
    }

    @Test
    void convertTicket() {
        DataEquals.assertTicketEquals(DataSamples.ticket,convert(convert(DataSamples.ticket)));
        DataEquals.assertTicketEquals(DataSamples.nullTicket,convert(convert(DataSamples.nullTicket)));
    }

    @Test
    void convertTrip() {
        DataEquals.assertTripEquals(DataSamples.trip,convert(convert(DataSamples.trip)));
        DataEquals.assertTripEquals(DataSamples.nullTrip,convert(convert(DataSamples.nullTrip)));
    }

    @Test
    void convertTrain() {
        DataEquals.assertTrainEquals(DataSamples.train,convert(convert(DataSamples.train)));
        DataEquals.assertTrainEquals(DataSamples.nullTrain,convert(convert(DataSamples.nullTrain)));
    }

    @Test
    void convertStation() {
        DataEquals.assertStationEquals(DataSamples.station,convert(convert(DataSamples.station)));
        DataEquals.assertStationEquals(DataSamples.station2,convert(convert(DataSamples.station2)));
        DataEquals.assertStationEquals(DataSamples.nullStation,convert(convert(DataSamples.nullStation)));
        DataEquals.assertStationEquals(DataSamples.nullStation2,convert(convert(DataSamples.nullStation2)));
    }

    @Test
    void convertRoute() {
        DataEquals.assertRouteEquals(DataSamples.route,convert(convert(DataSamples.route)));
        DataEquals.assertRouteEquals(DataSamples.nullRoute,convert(convert(DataSamples.nullRoute)));
    }

    @Test
    void convertTrainType() {
        DataEquals.assertTrainTypeEquals(DataSamples.trainType,convert(convert(DataSamples.trainType)));
        DataEquals.assertTrainTypeEquals(DataSamples.nullTrainType,convert(convert(DataSamples.nullTrainType)));
    }

}