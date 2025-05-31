package it.trenical.server.gui;

import it.trenical.common.TrainType;
import it.trenical.server.observer.TrainTypesCache;

public class ManagementTrainTypesPanel extends ManagementPanel<TrainType> implements TrainTypesCache.Observer {

    ManagementTrainTypesPanel() {
        super("Tipi di treno", "Tipo di treno");
        server.trainTypesCacheObs.attach(this);
    }

    @Override
    public void updateTrainTypesCache() {
        updateItemsList(server.getTrainTypesCache());
    }

    @Override
    void createDialog() {
        new CreateTrainTypeDialog().showDialog(mainFrame);
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editTrainType");
    }

    @Override
    void deleteDialog() {
        new DeleteTrainTypeDialog(itemsList.getSelectedValue()).showDialog(mainFrame);
    }
}