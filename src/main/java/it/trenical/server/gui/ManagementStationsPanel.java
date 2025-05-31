package it.trenical.server.gui;

import it.trenical.common.Station;
import it.trenical.server.observer.StationsCache;

public class ManagementStationsPanel extends ManagementPanel<Station> implements StationsCache.Observer {

    ManagementStationsPanel() {
        super("Stazioni", "Stazione");
        server.stationsCacheObs.attach(this);
    }


    @Override
    void createDialog() {
        new CreateStationDialog().showDialog();
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editStation");
    }

    @Override
    void deleteDialog() {
        new DeleteStationDialog(itemsList.getSelectedValue()).showDialog();
    }

    @Override
    public void updateStationsCache() {
        updateItemsList(server.getStationsCache());
    }
}