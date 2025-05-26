package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.observer.TicketsCache;
import it.trenical.common.Ticket;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Collection;

public class CustomerAreaPanel implements TicketsCache.Observer {

    private JPanel main;
    private JList<Ticket> ticketList;

    private final DefaultListModel<Ticket> ticketListModel;

    CustomerAreaPanel() {
        Client client = Client.getInstance();
        client.ticketsCacheSub.attach(this);

        ticketListModel = new DefaultListModel<>();
        ticketList.setCellRenderer(new MultilineCellRenderer());
        ticketList.setModel(ticketListModel);
    }

    JPanel getPanel() {
        return main;
    }

    @Override
    public void updateTicketsCache(Collection<Ticket> cache) {
        ticketListModel.clear();
        ticketListModel.addAll(cache);
    }

    private static class MultilineCellRenderer extends JTextArea implements ListCellRenderer<Object> {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            if (value instanceof Ticket t) {
                Calendar cal = t.getTrip().getDepartureTime();
                String date = String.format("Giorno %d/%d/%d ore %d:%d",
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.HOUR),
                        cal.get(Calendar.MINUTE)
                );
                value = String.format("## %s da %s a %s ##\n%s\n%s %s",
                        (t.isPaid() ? "Biglietto" : "Prenotazione"),
                        t.getTrip().getRoute().getDepartureStation().getTown(),
                        t.getTrip().getRoute().getArrivalStation().getTown(),
                        date,
                        t.getName(),t.getSurname()
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
