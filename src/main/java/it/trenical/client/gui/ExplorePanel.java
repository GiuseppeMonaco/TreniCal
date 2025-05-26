package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.StationsCache;
import it.trenical.client.observer.TrainTypesCache;
import it.trenical.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

class ExplorePanel implements
        TrainTypesCache.Observer,
        StationsCache.Observer{
    private JComboBox<Station> departureStation;
    private JComboBox<Station> arrivalStation;
    private JSpinner departureTime;
    private JSpinner numTicket;
    private JComboBox<TrainType> trainType;
    private JButton buttonSearch;
    private JPanel main;
    private JButton buttonRefresh;

    private final Client client;

    private static final String ANY_TRAIN_TYPE_LABEL = "Qualsiasi";

    ExplorePanel() {
        client = Client.getInstance();
        client.stationsCacheSub.attach(this);
        client.trainTypesCacheSub.attach(this);

        buttonSearch.addActionListener(actionEvent -> onButtonSearch());
        buttonSearch.setEnabled(false);

        buttonRefresh.addActionListener(actionEvent -> onButtonRefresh());

        departureStation.setRenderer(new CustomListCellRenderer());
        departureStation.setSelectedItem(null);
        departureStation.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonSearch.setEnabled(canEnableSearchButton());
        });

        arrivalStation.setRenderer(new CustomListCellRenderer());
        arrivalStation.setSelectedItem(null);
        arrivalStation.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonSearch.setEnabled(canEnableSearchButton());
        });

        trainType.setRenderer(new CustomListCellRenderer());
        trainType.addItem(new TrainTypeData(ANY_TRAIN_TYPE_LABEL));
        trainType.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonSearch.setEnabled(canEnableSearchButton());
        });
    }

    private boolean canEnableSearchButton() {
        return trainType.getSelectedItem() != null &&
                departureStation.getSelectedItem() != null &&
                arrivalStation.getSelectedItem() != null;
    }

    private void createUIComponents() {
        numTicket = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        departureTime = new JSpinner(new SpinnerDateModel());
    }

    private void onButtonSearch() {
        if(!canEnableSearchButton()) return;
        Route r = new RouteData(
                (Station) departureStation.getSelectedItem(),
                (Station) arrivalStation.getSelectedItem()
        );

        Calendar c = Calendar.getInstance();
        c.setTime((Date) departureTime.getValue());
        TripData.Builder tr = TripData.newBuilder(r)
                .setDepartureTime(c);

        if (trainType.getSelectedIndex() != 0)
            tr.setTrain(TrainData.newBuilder(-1).setType((TrainType) trainType.getSelectedItem()).build());

        MainFrame m = MainFrame.getInstance();
        m.queryTrips(tr.build());
    }

    private void onButtonRefresh() {
        buttonSearch.setEnabled(canEnableSearchButton());
        MainFrame m = MainFrame.getInstance();
        try {
            client.queryStations();
            client.queryTrainTypes();
        } catch (UnreachableServer e) {
            m.unreachableServerDialog();
        }
    }

    JPanel getPanel() {
        return main;
    }

    @Override
    public void updateStationsCache(Collection<Station> cache) {
        Station currentDepartureValue = (Station) departureStation.getSelectedItem();
        Station currentArrivalValue = (Station) arrivalStation.getSelectedItem();
        departureStation.removeAllItems();
        cache.forEach(departureStation::addItem);
        departureStation.setSelectedItem(currentDepartureValue);
        arrivalStation.removeAllItems();
        cache.forEach(arrivalStation::addItem);
        arrivalStation.setSelectedItem(currentArrivalValue);
    }

    @Override
    public void updateTrainTypesCache(Collection<TrainType> cache) {
        TrainType currentValue = (TrainType) trainType.getSelectedItem();
        trainType.removeAllItems();
        trainType.addItem(new TrainTypeData(ANY_TRAIN_TYPE_LABEL));
        cache.forEach(trainType::addItem);
        trainType.setSelectedItem(currentValue);
    }

    /**
     * This class is nedeed to change render behaviour of JComboBox component.
     */
    private static class CustomListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Station) {
                value = ((Station) value).getName();
            } else if (value instanceof TrainType) {
                value = ((TrainType) value).getName();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

    }
}
