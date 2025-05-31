package it.trenical.client.gui;

import it.trenical.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

public class EditTicketDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonConfirm;
    private JButton buttonCancel;
    private JLabel routeLabel;
    private JLabel dateLabel;
    private JLabel totalLabel;
    private JLabel promoLabel;
    private JLabel passengerLabel;
    private JLabel classLabel;
    private JLabel titleLabel;
    private JLabel priceWarningLabel;

    static private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    static private final String PAYWARNING = """
            <html><div style='text-align: center;'>
            Attenzione, il nuovo biglietto ha un costo superiore rispetto al precedente.<br>
            Dovrai perciò pagare la differenza.<br>
            Cliccando su conferma accetti di effettuare il pagamento.<br>
            </div></html>
            """;

    static private final String LOSSWARNING = """
            <html><div style='text-align: center;'>
            Attenzione, il nuovo biglietto ha un costo inferiore rispetto al precedente.<br>
            Non verrà effettuato alcun rimborso.<br>
            Cliccando su conferma accetti di cambiare classe del biglietto.<br>
            </div></html>
            """;

    private final MainFrame mainFrame;

    // New ticket
    private final Ticket newTicket;

    public EditTicketDialog(Ticket ticket) {
        mainFrame = MainFrame.getInstance();

        if(ticket == null) throw new IllegalArgumentException("ticket cannot be null");

        Ticket tempTicket = TicketData.newBuilder(ticket.getId())
                .setName(ticket.getName())
                .setSurname(ticket.getSurname())
                .setUser(ticket.getUser())
                .setTrip(ticket.getTrip())
                .setBusiness(!ticket.isBusiness())
                .setPromotion(ticket.getPromotion())
                .setPaid(ticket.isPaid())
                .build();

        newTicket = TicketData.newBuilder(tempTicket.getId())
                .setName(ticket.getName())
                .setSurname(ticket.getSurname())
                .setUser(ticket.getUser())
                .setTrip(ticket.getTrip())
                .setBusiness(!ticket.isBusiness())
                .setPromotion(ticket.getPromotion())
                .setPrice(tempTicket.calculatePrice())
                .setPaid(ticket.isPaid())
                .build();

        Trip tr = newTicket.getTrip();
        Route ro = tr.getRoute();

        float toPay = newTicket.getPrice() - ticket.getPrice();
        float displayPrice = toPay <= 0 ? 0f : toPay;

        routeLabel.setText(String.format(
                routeLabel.getText(),
                ro.getDepartureStation().getTown(),
                ro.getArrivalStation().getTown()
        ));
        dateLabel.setText(String.format(
                dateLabel.getText(),
                dateFormatter.format(tr.getDepartureTime().getTime())
        ));
        if(!ticket.isPaid()) totalLabel.setVisible(false);
        totalLabel.setText(String.format(
                totalLabel.getText(),
                displayPrice
        ));
        Promotion pr = newTicket.getPromotion();
        promoLabel.setText(String.format(
                promoLabel.getText(),
                pr != null ? pr.getCode() : "Nessuna"
        ));
        passengerLabel.setText(String.format(
                passengerLabel.getText(),
                newTicket.getName(),
                newTicket.getSurname()
        ));
        classLabel.setText(String.format(
                classLabel.getText(),
                newTicket.isBusiness() ? "Business" : "Economy"
        ));
        titleLabel.setText(String.format(
                titleLabel.getText(),
                newTicket.isBusiness() ? "Business" : "Economy"
        ));

        priceWarningLabel.setForeground(Color.RED);
        priceWarningLabel.setVisible(false);
        if(toPay > 0) {
            priceWarningLabel.setText(PAYWARNING);
            if(ticket.isPaid()) priceWarningLabel.setVisible(true);
        } else if (toPay < 0) {
            priceWarningLabel.setText(LOSSWARNING);
            if(ticket.isPaid()) priceWarningLabel.setVisible(true);
        }


        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonConfirm);

        buttonConfirm.addActionListener(e -> onConfirm());

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

    private void onConfirm() {
        mainFrame.editTicket(newTicket,this);
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
