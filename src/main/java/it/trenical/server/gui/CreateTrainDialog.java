package it.trenical.server.gui;

import it.trenical.common.TrainData;
import it.trenical.common.TrainType;
import it.trenical.server.db.exceptions.ForeignKeyException;
import it.trenical.server.db.exceptions.PrimaryKeyException;
import it.trenical.server.observer.TrainTypesCache;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreateTrainDialog extends CreateDialog implements TrainTypesCache.Observer {
    private JPanel main;
    private JTextField idField;
    private JTextField businessCapacityField;
    private JTextField economyCapacityField;
    private JComboBox<TrainType> trainTypesBox;

    public CreateTrainDialog() {
        super();
        addToFormPane(main);

        updateTrainTypesCache();
        trainTypesBox.setRenderer(new CustomListCellRenderer());
        trainTypesBox.setSelectedItem(null);
        trainTypesBox.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonCreate.setEnabled(canCreateButtonBeEnabled());
        });

        idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        businessCapacityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        economyCapacityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
    }

    @Override
    boolean canCreateButtonBeEnabled() {
        return isIdValid() &&
                isTypeValid() &&
                isEconomyCapValid() &&
                isBusinessCapValid();
    }

    private boolean isIdValid() {
        String text = idField.getText();
        return !text.isBlank() && parseInteger(text) > -1;
    }

    private boolean isTypeValid() {
        return trainTypesBox.getSelectedItem() instanceof TrainType;
    }

    private boolean isEconomyCapValid() {
        String text = economyCapacityField.getText();
        return !text.isBlank() && parseInteger(text) > -1;
    }

    private boolean isBusinessCapValid() {
        String text = businessCapacityField.getText();
        return !text.isBlank() && parseInteger(text) > -1;
    }

    @Override
    void createItem() {
        try {
            server.createTrain(TrainData.newBuilder(parseInteger(idField.getText()))
                    .setType((TrainType) trainTypesBox.getSelectedItem())
                    .setEconomyCapacity(parseInteger(economyCapacityField.getText()))
                    .setBusinessCapacity(parseInteger(businessCapacityField.getText()))
                    .build()
            );
        } catch (PrimaryKeyException e) {
            mainFrame.violatedPrimaryKeyDialog();
            return;
        } catch (ForeignKeyException e) {
            mainFrame.violatedForeignKeyDialog();
            return;
        }
        mainFrame.itemSuccessfullyAddedDialog();
        dispose();
    }

    @Override
    public void updateTrainTypesCache() {
        trainTypesBox.removeAllItems();
        server.getTrainTypesCache().forEach(trainTypesBox::addItem);
    }
}
