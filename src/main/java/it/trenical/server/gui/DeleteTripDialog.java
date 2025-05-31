package it.trenical.server.gui;

import it.trenical.common.Route;
import it.trenical.common.Train;
import it.trenical.common.Trip;

import javax.swing.*;

import java.text.SimpleDateFormat;

import static it.trenical.common.gui.Util.formatLabel;

public class DeleteTripDialog extends DeleteDialog<Trip> {

    private JPanel main;
    private JLabel trainLabel;
    private JLabel dateLabel;
    private JLabel economySeatsLabel;
    private JLabel routeLabel;
    private JLabel businessSeatsLabel;

    static private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public DeleteTripDialog(Trip item) {
        super(item);
        addToInfoPane(main);

        Route r = item.getRoute();
        Train tr = item.getTrain();
        formatLabel(trainLabel,tr.getId(), tr.getType().getName());
        formatLabel(dateLabel,dateFormatter.format(item.getDepartureTime().getTime()));
        formatLabel(routeLabel,r.getDepartureStation().getTown(),r.getArrivalStation().getTown());
        formatLabel(economySeatsLabel,item.getAvailableEconomySeats());
        formatLabel(businessSeatsLabel,item.getAvailableBusinessSeats());
    }

    @Override
    void deleteItem() {
        server.deleteTrip(itemToDelete);
    }
}
