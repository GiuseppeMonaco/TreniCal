package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.observer.TicketsCache;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class CustomerAreaFrame extends JFrame implements TicketsCache.Observer {
    private JList<Ticket> ticketList;
    private JPanel mainPanel;
    private JButton buttonClose;

    static private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final DefaultListModel<Ticket> ticketListModel;

    private final MainFrame mainFrame;
    private final Client client;

    CustomerAreaFrame() {
        mainFrame = MainFrame.getInstance();
        client = mainFrame.getClient();
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
    public void updateTicketsCache() {
        Collection<Ticket> cache = client.getTicketsCache();
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
                Trip tr = t.getTrip();

                String date = dateFormatter.format(tr.getDepartureTime().getTime());

                value = String.format("## %s da %s a %s ##\nGiorno %s\n%s %s - %s class\n",
                        t.isPaid() ? "Biglietto" : "Prenotazione",
                        tr.getRoute().getDepartureStation().getTown(),
                        tr.getRoute().getArrivalStation().getTown(),
                        date,
                        t.getName(),t.getSurname(),
                        t.isBusiness() ? "Business" : "Economy"
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
