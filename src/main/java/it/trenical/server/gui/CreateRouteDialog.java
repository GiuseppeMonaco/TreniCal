package it.trenical.server.gui;

import it.trenical.common.*;
import it.trenical.server.db.exceptions.ForeignKeyException;
import it.trenical.server.db.exceptions.PrimaryKeyException;
import it.trenical.server.observer.StationsCache;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class CreateRouteDialog extends CreateDialog implements StationsCache.Observer {
    private JPanel main;
    private JTextField distanceField;
    private JComboBox<Station> departureBox;
    private JComboBox<Station> arrivalBox;

    public CreateRouteDialog() {
        super();
        addToFormPane(main);

        updateStationsCache();
        departureBox.setRenderer(new CustomListCellRenderer());
        departureBox.setSelectedItem(null);
        departureBox.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonCreate.setEnabled(canCreateButtonBeEnabled());
        });

        arrivalBox.setRenderer(new CustomListCellRenderer());
        arrivalBox.setSelectedItem(null);
        arrivalBox.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonCreate.setEnabled(canCreateButtonBeEnabled());
        });

        distanceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
    }

    @Override
    boolean canCreateButtonBeEnabled() {
        return isDepartureValid() &&
                isArrivalValid() &&
                isDistanceValid();
    }

    private boolean isDistanceValid() {
        String text = distanceField.getText();
        return !text.isBlank() && parseInteger(text) > -1;
    }

    private boolean isDepartureValid() {
        return departureBox.getSelectedItem() instanceof Station;
    }

    private boolean isArrivalValid() {
        return arrivalBox.getSelectedItem() instanceof Station;
    }

    @Override
    void createItem() {
        try {
            server.createRoute(new RouteData(
                    (Station) departureBox.getSelectedItem(),
                    (Station) arrivalBox.getSelectedItem(),
                    parseInteger(distanceField.getText())
            ));
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
    public void updateStationsCache() {
        departureBox.removeAllItems();
        arrivalBox.removeAllItems();
        Collection<Station> st = server.getStationsCache();
        st.forEach(departureBox::addItem);
        st.forEach(arrivalBox::addItem);
    }
}
