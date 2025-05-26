package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.observer.TripsCache;
import it.trenical.common.Trip;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Calendar;
import java.util.Collection;

public class TripsPanel implements TripsCache.Observer {
    private JPanel main;
    private JList<Trip> tripsList;
    private JButton buttonBack;

    private final DefaultListModel<Trip> tripsListModel;

    public TripsPanel() {
        Client c = Client.getInstance();
        c.filteredTripsCacheSub.attach(this);

        tripsListModel = new DefaultListModel<>();
        tripsList.setModel(tripsListModel);
        tripsList.setCellRenderer(new MultilineCellRenderer());

        buttonBack.addActionListener(actionEvent -> onButtonBack());
    }

    private void onButtonBack() {
        MainFrame m = MainFrame.getInstance();
        m.showExplorePanel();
    }

    JPanel getPanel() {
        return main;
    }

    @Override
    public void updateTripsCache(Collection<Trip> cache) {
        tripsListModel.clear();
        if (cache.isEmpty()) {
            if (main.getBorder() instanceof TitledBorder tb) {
                tb.setTitle("Nessun viaggio trovato");
            }
            return;
        }
        if (main.getBorder() instanceof TitledBorder tb) {
            tb.setTitle("Seleziona il tuo prossimo viaggio");
        }
        tripsListModel.addAll(cache);
    }

    private static class MultilineCellRenderer extends JTextArea implements ListCellRenderer<Object> {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            if (value instanceof Trip t) {
                Calendar cal = t.getDepartureTime();
                String date = String.format("Giorno %d/%d/%d ore %d:%d",
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.HOUR),
                        cal.get(Calendar.MINUTE)
                );
                value = String.format("## Da %s a %s ##\n%s\nTreno: %s",
                        t.getRoute().getDepartureStation().getTown(),
                        t.getRoute().getArrivalStation().getTown(),
                        date,
                        t.getTrain().getType().getName()
                );
            }

            setText(value.toString());
            setFont(list.getFont());
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }
}
