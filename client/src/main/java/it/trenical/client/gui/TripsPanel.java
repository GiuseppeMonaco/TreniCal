package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.observer.TripsCache;
import it.trenical.common.Trip;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class TripsPanel implements TripsCache.Observer {
    private JPanel main;
    private JList<Trip> tripsList;
    private JButton buttonBack;
    private JButton buttonConfirm;

    static private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final DefaultListModel<Trip> tripsListModel;

    private final Client client;
    private final MainFrame mainFrame;

    public TripsPanel() {
        mainFrame = MainFrame.getInstance();
        client = mainFrame.getClient();
        client.filteredTripsCacheSub.attach(this);

        tripsListModel = new DefaultListModel<>();
        tripsList.setModel(tripsListModel);
        tripsList.setCellRenderer(new MultilineCellRenderer());
        tripsList.addListSelectionListener(listSelectionEvent -> buttonConfirm.setEnabled(canButtonConfirmBeEnabled()));

        buttonBack.addActionListener(actionEvent -> onButtonBack());

        buttonConfirm.addActionListener(actionEvent -> onButtonConfirm());
    }

    private void onButtonConfirm() {
        if(!canButtonConfirmBeEnabled()) return;
        client.setCurrentTrip(tripsList.getSelectedValue());
        mainFrame.showCheckoutPanel();
    }

    private void onButtonBack() {
        mainFrame.showExplorePanel();
    }

    private boolean canButtonConfirmBeEnabled() {
        return !tripsList.isSelectionEmpty();
    }

    JPanel getPanel() {
        return main;
    }

    @Override
    public void updateTripsCache() {
        Collection<Trip> cache = client.getFilteredTripsCache();
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
                String date = dateFormatter.format(t.getDepartureTime().getTime());

                value = String.format("## Da %s a %s ##\nGiorno %s\nTreno: %s\n",
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
