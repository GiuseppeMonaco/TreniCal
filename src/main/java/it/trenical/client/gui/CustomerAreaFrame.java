package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.observer.TicketsCache;
import it.trenical.common.Ticket;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Collection;

public class CustomerAreaFrame extends JFrame implements TicketsCache.Observer {
    private JList<Ticket> ticketList;
    private JPanel mainPanel;
    private JButton buttonClose;

    private final DefaultListModel<Ticket> ticketListModel;

    CustomerAreaFrame() {
        Client client = Client.getInstance();
        client.ticketsCacheSub.attach(this);

        setContentPane(mainPanel);
        setTitle("TreniCal - Area Personale");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        ticketListModel = new DefaultListModel<>();
        ticketList.setCellRenderer(new MultilineCellRenderer());
        ticketList.setModel(ticketListModel);

        buttonClose.addActionListener(actionEvent -> onButtonClose());
    }

    private void onButtonClose() {
        close();
    }

    @Override
    public void updateTicketsCache(Collection<Ticket> cache) {
        ticketListModel.clear();
        ticketListModel.addAll(cache);
    }

    void display() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void close() {
        dispose();
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
