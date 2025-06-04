package it.trenical.client.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.trenical.client.Client;
import it.trenical.common.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

        if (ticket == null) throw new IllegalArgumentException("ticket cannot be null");

        this.newTicket = TicketData.newBuilder(ticket.getId())
                .setName(ticket.getName())
                .setSurname(ticket.getSurname())
                .setUser(ticket.getUser())
                .setTrip(ticket.getTrip())
                .setBusiness(!ticket.isBusiness())
                .setPromotion(ticket.getPromotion())
                .setPaid(ticket.isPaid())
                .build();

        float newPrice = Client.getInstance().getTicketPrice(newTicket);

        Trip tr = newTicket.getTrip();
        Route ro = tr.getRoute();

        float toPay = newPrice - ticket.getPrice();
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
        if (!ticket.isPaid()) totalLabel.setVisible(false);
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
        if (toPay > 0) {
            priceWarningLabel.setText(PAYWARNING);
            if (ticket.isPaid()) priceWarningLabel.setVisible(true);
        } else if (toPay < 0) {
            priceWarningLabel.setText(LOSSWARNING);
            if (ticket.isPaid()) priceWarningLabel.setVisible(true);
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
        mainFrame.editTicket(newTicket, this);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Annulla");
        panel2.add(buttonCancel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonConfirm = new JButton();
        buttonConfirm.setText("Conferma");
        panel2.add(buttonConfirm, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Sommario", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(7, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        routeLabel = new JLabel();
        routeLabel.setText("Da %s a %s");
        panel5.add(routeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dateLabel = new JLabel();
        dateLabel.setText("Data: %s");
        panel5.add(dateLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        totalLabel = new JLabel();
        totalLabel.setText("Totale: %.2f€");
        panel5.add(totalLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        promoLabel = new JLabel();
        promoLabel.setText("Promozione applicata: %s");
        panel5.add(promoLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passengerLabel = new JLabel();
        passengerLabel.setText("Passeggero: %s %s");
        panel5.add(passengerLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        classLabel = new JLabel();
        classLabel.setText("Classe: %s");
        panel5.add(classLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        titleLabel = new JLabel();
        titleLabel.setText("Vuoi davvero passare alla %s Class?");
        panel3.add(titleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        priceWarningLabel = new JLabel();
        priceWarningLabel.setText("%s");
        panel3.add(priceWarningLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
