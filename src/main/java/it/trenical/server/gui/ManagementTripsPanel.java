package it.trenical.server.gui;

import it.trenical.common.Trip;
import it.trenical.server.observer.TripsCache;

public class ManagementTripsPanel extends ManagementPanel<Trip> implements TripsCache.Observer {

    ManagementTripsPanel() {
        super("Viaggi", "Viaggio");
        server.tripsCacheObs.attach(this);
    }

    @Override
    void createDialog() {
        new CreateTripDialog().showDialog();
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editTrip");
    }

    @Override
    void deleteDialog() {
        new DeleteTripDialog(itemsList.getSelectedValue()).showDialog();
    }

    @Override
    public void updateTripsCache() {
        updateItemsList(server.getTripsCache());
    }
}
