package it.trenical.server.gui;

import it.trenical.common.TrainType;

import javax.swing.*;

public class DeleteTrainTypeDialog extends DeleteDialog<TrainType> {

    private JPanel main;
    private JLabel nameLabel;
    private JLabel priceLabel;

    public DeleteTrainTypeDialog(TrainType item) {
        super(item);
        addToInfoPane(main);
        nameLabel.setText(String.format(nameLabel.getText(),item.getName()));
        priceLabel.setText(String.format(priceLabel.getText(),item.getPrice()));
    }

    @Override
    void deleteItem() {
        server.deleteTrainType(itemToDelete);
    }
}
