package it.trenical.server.gui;

import it.trenical.common.Train;

import javax.swing.*;

import static it.trenical.common.gui.Util.formatLabel;

public class DeleteTrainDialog extends DeleteDialog<Train> {

    private JPanel main;
    private JLabel idLabel;
    private JLabel typeLabel;
    private JLabel economyCapLabel;
    private JLabel businessCapLabel;

    public DeleteTrainDialog(Train item) {
        super(item);
        addToInfoPane(main);
        formatLabel(idLabel,item.getId());
        formatLabel(typeLabel,item.getType().getName());
        formatLabel(economyCapLabel,item.getEconomyCapacity());
        formatLabel(businessCapLabel,item.getBusinessCapacity());

    }

    @Override
    void deleteItem() {
        server.deleteTrain(itemToDelete);
    }
}
