package it.trenical.server.gui;

import it.trenical.common.Route;

import javax.swing.*;

import static it.trenical.common.gui.Util.formatLabel;

public class DeleteRouteDialog extends DeleteDialog<Route> {

    private JPanel main;
    private JLabel departureLabel;
    private JLabel arrivalLabel;
    private JLabel distanceLabel;

    public DeleteRouteDialog(Route item) {
        super(item);
        addToInfoPane(main);
        formatLabel(departureLabel,item.getDepartureStation().getTown());
        formatLabel(arrivalLabel,item.getDepartureStation().getTown());
        formatLabel(distanceLabel,item.getDistance());

    }

    @Override
    void deleteItem() {
        server.deleteRoute(itemToDelete);
    }
}
