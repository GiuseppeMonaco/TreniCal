package it.trenical.server.gui;

import it.trenical.common.StationData;
import it.trenical.server.db.exceptions.PrimaryKeyException;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreateStationDialog extends CreateDialog {
    private JPanel main;
    private JTextField nameField;
    private JTextField townField;
    private JTextField provinceField;
    private JTextField addressField;

    public CreateStationDialog() {
        super();
        addToFormPane(main);

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        townField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        provinceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        addressField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
    }

    private boolean isNameValid() {
        return !nameField.getText().isBlank();
    }

    private boolean isTownValid() {
        return !townField.getText().isBlank();
    }

    private boolean isAddressValid() {
        return !addressField.getText().isBlank();
    }

    private boolean isProvinceValid() {
        return !provinceField.getText().isBlank();
    }

    @Override
    boolean canCreateButtonBeEnabled() {
        return isNameValid() &&
                isAddressValid() &&
                isProvinceValid() &&
                isTownValid();
    }

    @Override
    void createItem() {
        try {
            server.createStation(StationData.newBuilder(nameField.getText())
                    .setTown(townField.getText())
                    .setProvince(provinceField.getText())
                    .setAddress(addressField.getText())
                    .build()
            );
        } catch (PrimaryKeyException e) {
            mainFrame.violatedPrimaryKeyDialog();
            return;
        }
        mainFrame.itemSuccessfullyAddedDialog();
        dispose();
    }
}
