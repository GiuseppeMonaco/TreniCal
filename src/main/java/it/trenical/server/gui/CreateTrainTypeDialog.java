package it.trenical.server.gui;

import it.trenical.common.TrainTypeData;
import it.trenical.server.db.exceptions.PrimaryKeyException;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreateTrainTypeDialog extends CreateDialog {
    private JPanel main;
    private JTextField nameField;
    private JTextField priceField;

    public CreateTrainTypeDialog() {
        super();
        addToFormPane(main);

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        priceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                priceField.setText(priceField.getText().replace(",","."));
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
    }

    private boolean isNameValid() {
        return !nameField.getText().isBlank();
    }

    private boolean isPriceValid() {
        String text = priceField.getText();
        return !text.isBlank() && parseFloat(text) >= 0;
    }

    @Override
    boolean canCreateButtonBeEnabled() {
        return isNameValid() && isPriceValid();
    }

    @Override
    void createItem() {
        try {
            server.createTrainType(new TrainTypeData(
                    nameField.getText(),
                    parseFloat(priceField.getText())
            ));
        } catch (PrimaryKeyException e) {
            mainFrame.violatedPrimaryKeyDialog();
            return;
        }
        mainFrame.itemSuccessfullyAddedDialog();
        dispose();
    }
}
