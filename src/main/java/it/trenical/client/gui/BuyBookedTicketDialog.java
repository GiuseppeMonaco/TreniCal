package it.trenical.client.gui;

import it.trenical.common.Promotion;
import it.trenical.common.Route;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;

import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;

class BuyBookedTicketDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonBuy;
    private JButton buttonCancel;
    private JLabel routeLabel;
    private JLabel dateLabel;
    private JLabel totalLabel;
    private JLabel promoLabel;
    private JLabel passengerLabel;
    private JLabel classLabel;

    static private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final MainFrame mainFrame;

    // Ticket to buy
    private final Ticket ticket;

    public BuyBookedTicketDialog(Ticket ticket) {
        mainFrame = MainFrame.getInstance();

        if(ticket == null) throw new IllegalArgumentException("ticket cannot be null");
        this.ticket = ticket;

        Trip tr = ticket.getTrip();
        Route ro = tr.getRoute();
        routeLabel.setText(String.format(
                routeLabel.getText(),
                ro.getDepartureStation().getTown(),
                ro.getArrivalStation().getTown()
        ));
        dateLabel.setText(String.format(
                dateLabel.getText(),
                dateFormatter.format(tr.getDepartureTime().getTime())
        ));
        totalLabel.setText(String.format(
                totalLabel.getText(),
                ticket.getPrice()
        ));
        Promotion pr = ticket.getPromotion();
        promoLabel.setText(String.format(
                promoLabel.getText(),
                pr != null ? pr.getCode() : "Nessuna"
        ));
        passengerLabel.setText(String.format(
                passengerLabel.getText(),
                ticket.getName(),
                ticket.getSurname()
        ));
        classLabel.setText(String.format(
                classLabel.getText(),
                ticket.isBusiness() ? "Business" : "Economy"
        ));


        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonBuy);

        buttonBuy.addActionListener(e -> onBuy());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onBuy() {
        Collection<Ticket> req = new LinkedList<>();
        req.add(ticket);
        mainFrame.payBookedTickets(req);
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
