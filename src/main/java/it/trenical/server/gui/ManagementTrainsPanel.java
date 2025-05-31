package it.trenical.server.gui;

import it.trenical.common.Train;
import it.trenical.server.observer.TrainsCache;

public class ManagementTrainsPanel extends ManagementPanel<Train> implements TrainsCache.Observer {

    ManagementTrainsPanel() {
        super("Treni", "Treno");
        server.trainsCacheObs.attach(this);
    }

    @Override
    void createDialog() {
        new CreateTrainDialog().showDialog(mainFrame);
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editTrain");
    }

    @Override
    void deleteDialog() {
        new DeleteTrainDialog(itemsList.getSelectedValue()).showDialog(mainFrame);
    }

    @Override
    public void updateTrainsCache() {
        updateItemsList(server.getTrainsCache());
    }
}
