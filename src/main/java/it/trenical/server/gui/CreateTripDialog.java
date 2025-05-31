package it.trenical.server.gui;

import it.trenical.common.*;
import it.trenical.server.db.exceptions.ForeignKeyException;
import it.trenical.server.db.exceptions.PrimaryKeyException;
import it.trenical.server.observer.RoutesCache;
import it.trenical.server.observer.TrainsCache;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Calendar;
import java.util.Date;

public class CreateTripDialog extends CreateDialog implements TrainsCache.Observer, RoutesCache.Observer {
    private JPanel main;
    private JComboBox<Train> trainBox;
    private JComboBox<Route> routeBox;
    private JSpinner departureTime;

    public CreateTripDialog() {
        super();
        addToFormPane(main);

        updateTrainsCache();
        trainBox.setRenderer(new CustomListCellRenderer());
        trainBox.setSelectedItem(null);
        trainBox.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonCreate.setEnabled(canCreateButtonBeEnabled());
        });

        updateRoutesCache();
        routeBox.setRenderer(new CustomListCellRenderer());
        routeBox.setSelectedItem(null);
        routeBox.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonCreate.setEnabled(canCreateButtonBeEnabled());
        });
    }

    @Override
    boolean canCreateButtonBeEnabled() {
        return isTrainValid() &&
                isRouteValid();
    }

    private boolean isTrainValid() {
        return trainBox.getSelectedItem() instanceof Train;
    }

    private boolean isRouteValid() {
        return routeBox.getSelectedItem() instanceof Route;
    }

    @Override
    void createItem() {
        Calendar c = Calendar.getInstance();
        c.setTime((Date) departureTime.getValue());
        Train train = (Train) trainBox.getSelectedItem();
        assert train != null;
        try {
            server.createTrip(TripData.newBuilder((Route) routeBox.getSelectedItem())
                    .setTrain(train)
                    .setDepartureTime(c)
                    .setAvailableEconomySeats(train.getEconomyCapacity())
                    .setAvailableBusinessSeats(train.getBusinessCapacity())
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
    public void updateRoutesCache() {
        routeBox.removeAllItems();
        server.getRoutesCache().forEach(routeBox::addItem);
    }

    @Override
    public void updateTrainsCache() {
        trainBox.removeAllItems();
        server.getTrainsCache().forEach(trainBox::addItem);
    }

    private void createUIComponents() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date minDate = cal.getTime();
        cal.add(Calendar.YEAR, 500);
        Date maxDate = cal.getTime();

        departureTime = new JSpinner(new SpinnerDateModel(minDate, minDate, maxDate, Calendar.DAY_OF_MONTH));
    }
}
