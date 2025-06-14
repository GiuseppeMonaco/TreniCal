package it.trenical.server.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.trenical.common.*;
import it.trenical.server.db.exceptions.ForeignKeyException;
import it.trenical.server.db.exceptions.PrimaryKeyException;
import it.trenical.server.observer.RoutesCache;
import it.trenical.server.observer.TrainsCache;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
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
        $$$setupUI$$$();
        addToFormPane(main);

        updateTrainsCache();
        trainBox.setRenderer(new CustomListCellRenderer());
        trainBox.setSelectedItem(null);
        trainBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
            buttonCreate.setEnabled(canCreateButtonBeEnabled());
        });

        updateRoutesCache();
        routeBox.setRenderer(new CustomListCellRenderer());
        routeBox.setSelectedItem(null);
        routeBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() != ItemEvent.SELECTED) return;
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
        itemSuccessfullyAddedDialog();
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
        main.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        main.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Nuovo Viaggio", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Treno");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        trainBox = new JComboBox();
        panel1.add(trainBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Tratta");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        routeBox = new JComboBox();
        panel2.add(routeBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Data Partenza");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.add(departureTime, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

}
