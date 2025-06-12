package it.trenical.client.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.trenical.client.Client;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.StationsCache;
import it.trenical.client.observer.TrainTypesCache;
import it.trenical.common.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

class ExplorePanel implements
        TrainTypesCache.Observer,
        StationsCache.Observer {
    private JComboBox<Station> departureStation;
    private JComboBox<Station> arrivalStation;
    private JSpinner departureTime;
    private JSpinner numTicket;
    private JComboBox<TrainType> trainType;
    private JButton buttonSearch;
    private JPanel main;
    private JButton buttonRefresh;

    private final Client client;
    private final MainFrame mainFrame;

    private static final String ANY_TRAIN_TYPE_LABEL = "Qualsiasi";

    ExplorePanel() {
        $$$setupUI$$$();
        mainFrame = MainFrame.getInstance();

        client = mainFrame.getClient();
        client.stationsCacheSub.attach(this);
        client.trainTypesCacheSub.attach(this);


        buttonSearch.addActionListener(actionEvent -> onButtonSearch());
        buttonSearch.setEnabled(false);

        buttonRefresh.addActionListener(actionEvent -> onButtonRefresh());

        departureStation.setRenderer(new CustomListCellRenderer());
        departureStation.setSelectedItem(null);
        departureStation.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonSearch.setEnabled(canEnableSearchButton());
        });

        arrivalStation.setRenderer(new CustomListCellRenderer());
        arrivalStation.setSelectedItem(null);
        arrivalStation.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonSearch.setEnabled(canEnableSearchButton());
        });

        trainType.setRenderer(new CustomListCellRenderer());
        trainType.addItem(new TrainTypeData(ANY_TRAIN_TYPE_LABEL));
        trainType.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
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

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.HOUR, 1);
        Date minDate = cal.getTime();
        cal.add(Calendar.YEAR, 500);
        Date maxDate = cal.getTime();

        departureTime = new JSpinner(new SpinnerDateModel(minDate, minDate, maxDate, Calendar.DAY_OF_MONTH));
    }

    private void onButtonSearch() {
        if (!canEnableSearchButton()) return;
        client.setCurrentPassengersNumber((int) numTicket.getValue());

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

        mainFrame.queryTrips(tr.build());
        mainFrame.showTripsPanel();
    }

    private void onButtonRefresh() {
        buttonSearch.setEnabled(canEnableSearchButton());
        try {
            client.queryStations();
            client.queryTrainTypes();
        } catch (UnreachableServer e) {
            mainFrame.unreachableServerDialog();
        }
    }

    JPanel getPanel() {
        return main;
    }

    @Override
    public void updateStationsCache() {
        Collection<Station> cache = client.getStationsCache();
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
    public void updateTrainTypesCache() {
        Collection<TrainType> cache = client.getTrainTypesCache();
        TrainType currentValue = (TrainType) trainType.getSelectedItem();
        trainType.removeAllItems();
        trainType.addItem(new TrainTypeData(ANY_TRAIN_TYPE_LABEL));
        cache.forEach(trainType::addItem);
        trainType.setSelectedItem(currentValue);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        main.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Scegli la tua prossima destinazione!", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Partenza da", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        departureStation = new JComboBox();
        panel2.add(departureStation, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Arrivo a", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        arrivalStation = new JComboBox();
        panel3.add(arrivalStation, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Partenza");
        panel5.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel5.add(departureTime, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel6, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Passeggeri");
        panel6.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel6.add(numTicket, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel7, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Tipo treno");
        panel7.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        trainType = new JComboBox();
        panel7.add(trainType, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel4.add(spacer4, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        main.add(spacer5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel8, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonSearch = new JButton();
        buttonSearch.setText("Cerca");
        panel8.add(buttonSearch, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel8.add(spacer6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel8.add(spacer7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonRefresh = new JButton();
        buttonRefresh.setText("Aggiorna");
        panel8.add(buttonRefresh, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
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
