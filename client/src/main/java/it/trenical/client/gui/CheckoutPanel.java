package it.trenical.client.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.*;
import it.trenical.client.request.exceptions.InvalidSeatsNumberException;
import it.trenical.common.*;
import it.trenical.common.gui.GenericOKDialog;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;

public class CheckoutPanel implements
        Logout.Observer,
        PassengersNumber.Observer,
        CurrentTrip.Observer,
        CurrentPromotion.Observer,
        CurrentPrice.Observer {
    private JPanel main;
    private JPanel passengers;
    private JButton buttonBack;
    private JButton buttonConfirm;
    private JRadioButton radioButtonBuy;
    private JRadioButton radioButtonBook;
    private JScrollPane scrollPanePassengers;
    private JLabel totalLabel;
    private JTextField promoCodeField;
    private JButton buttonPromo;
    private JLabel promoValidLabel;
    private JLabel promoLabel;
    private JLabel passengersNumberLabel;
    private JLabel dateLabel;
    private JLabel routeLabel;

    static private final String BUY_LABEL = "Acquista";
    static private final String BOOK_LABEL = "Prenota";
    static private final int SCROLL_SPEED = 10;
    static private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final MainFrame mainFrame;
    private final Client client;

    private final Collection<PassengerData> passengersData = new LinkedList<>();

    private final String promoLabelDefault;
    private final String passengersNumberLabelDefault;
    private final String dataLabelDefault;
    private final String routeLabelDefault;
    private final String totalLabelDefault;

    CheckoutPanel() {
        mainFrame = MainFrame.getInstance();
        client = mainFrame.getClient();
        client.passengersNumberSub.attach(this);
        client.currentTripSub.attach(this);
        client.currentPromoSub.attach(this);
        client.currentPriceSub.attach(this);
        client.logoutSub.attach(this);

        promoLabelDefault = promoLabel.getText();
        passengersNumberLabelDefault = passengersNumberLabel.getText();
        dataLabelDefault = dateLabel.getText();
        routeLabelDefault = routeLabel.getText();
        totalLabelDefault = totalLabel.getText();

        passengers.setLayout(new BoxLayout(passengers, BoxLayout.Y_AXIS));
        scrollPanePassengers.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        scrollPanePassengers.getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);

        buttonBack.addActionListener(actionEvent -> onButtonBack());

        buttonConfirm.addActionListener(actionEvent -> onButtonConfirm());
        radioButtonBuy.addActionListener(actionEvent -> onRadioButtonBuy());
        radioButtonBook.addActionListener(actionEvent -> onRadioButtonBook());

        buttonPromo.addActionListener(actionEvent -> onButtonPromo());

        promoValidLabel.setVisible(false);
        promoLabel.setText(String.format(promoLabelDefault, "Nessuna"));
        promoCodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                promoCodeField.setText(promoCodeField.getText().toUpperCase());
                buttonPromo.setEnabled(canButtonPromoBeEnabled());
            }
        });
    }

    private void onButtonPromo() {
        if (!canButtonPromoBeEnabled()) return;
        if (!client.isAuthenticated()) {
            mainFrame.loginDialog();
            return;
        }
        mainFrame.setCurrentPromotion(PromotionData.newBuilder(promoCodeField.getText()).build());
    }

    private boolean canButtonPromoBeEnabled() {
        return !promoCodeField.getText().isBlank();
    }

    private void onRadioButtonBuy() {
        if (!radioButtonBuy.isSelected()) return;
        buttonConfirm.setText(BUY_LABEL);
    }

    private void onRadioButtonBook() {
        if (!radioButtonBook.isSelected()) return;
        buttonConfirm.setText(BOOK_LABEL);
    }

    void onButtonBack() {
        mainFrame.showTripsPanel();
    }

    void onButtonConfirm() {
        if (!canEnableConfirm()) return;
        if (!client.isAuthenticated()) {
            mainFrame.loginDialog();
            return;
        }

        boolean isBook = radioButtonBook.isSelected();
        assert isBook != radioButtonBuy.isSelected();

        User u = client.getCurrentUser();
        Trip tr = client.getCurrentTrip();
        if (tr == null) return;

        Collection<Ticket> tickets = passengersData.stream().map((pd) ->
                (Ticket) TicketData.newBuilder(-1)
                        .setUser(u)
                        .setTrip(tr)
                        .setName(pd.getPassengerName())
                        .setSurname(pd.getPassengerSurname())
                        .setBusiness(pd.isBusinessClass())
                        .setPromotion(client.getCurrentPromotion())
                        .build()
        ).toList();

        String dialogMessage;
        try {
            if (isBook) {
                client.bookTickets(tickets);
                dialogMessage = "Biglietti prenotati con successo!";
            } else {
                client.buyTickets(tickets);
                dialogMessage = "Biglietti acquistati con successo!";
            }
            client.setCurrentPromotion(null);
        } catch (UnreachableServer e) {
            mainFrame.unreachableServerDialog();
            return;
        } catch (InvalidSessionTokenException e) {
            mainFrame.invalidTokenDialog();
            return;
        } catch (InvalidSeatsNumberException e) {
            mainFrame.invalidSeatsNumberDialog();
            return;
        }
        promoValidLabel.setVisible(false);

        GenericOKDialog.showDialog(mainFrame, dialogMessage);
        mainFrame.showExplorePanel();
    }


    JPanel getPanel() {
        return main;
    }

    boolean canEnableConfirm() {
        return passengersData.stream().allMatch(PassengerData::areFieldsValid);
    }

    @Override
    public void updateOnPassengersNumber() {
        int passNum = client.getCurrentPassengersNumber();
        passengersNumberLabel.setText(String.format(passengersNumberLabelDefault, passNum));
        passengers.removeAll();
        passengersData.clear();
        for (int i = 1; i <= passNum; i++) {
            PassengerData p = new PassengerData(i);
            passengers.add(p);
            passengersData.add(p);
        }
        buttonConfirm.setEnabled(false);
    }

    @Override
    public void updateOnCurrentTrip() {
        Trip trip = client.getCurrentTrip();

        String departureStation = "None";
        String arrivalStation = "None";
        String date = "None";
        if (trip != null) {
            Route r = trip.getRoute();
            departureStation = r.getDepartureStation().getTown();
            arrivalStation = r.getArrivalStation().getTown();
            date = dateFormatter.format(trip.getDepartureTime().getTime());
        }
        routeLabel.setText(String.format(routeLabelDefault, departureStation, arrivalStation));
        dateLabel.setText(String.format(dataLabelDefault, date));
    }

    @Override
    public void updateOnCurrentPrice() {
        totalLabel.setText(String.format(totalLabelDefault, client.getCurrentTotalPrice()));
    }

    @Override
    public void updateOnCurrentPromotion() {
        Promotion promo = client.getCurrentPromotion();

        String promoString = "Nessuna";
        String promoValidString = "Promozione non valida!";
        Color promoValidColor = Color.RED;
        if (promo != null) {
            if (!promo.isOnlyFidelityUser() || client.getCurrentUser().isFidelity()) {
                promoString = promo.getCode();
                promoValidString = "Promozione applicata!";
                promoValidColor = Color.GREEN;
            }
        }
        buttonPromo.setEnabled(false);
        promoCodeField.setText(null);
        promoLabel.setText(String.format(promoLabelDefault, promoString));
        promoValidLabel.setText(promoValidString);
        promoValidLabel.setForeground(promoValidColor);
        promoValidLabel.setVisible(true);
    }

    @Override
    public void updateOnLogout() {
        promoValidLabel.setText(null);
        promoValidLabel.setVisible(false);
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
        main = new JPanel();
        main.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        main.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Dati e Pagamento", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        buttonBack = new JButton();
        buttonBack.setText("Indietro");
        panel2.add(buttonBack, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonConfirm = new JButton();
        buttonConfirm.setEnabled(false);
        buttonConfirm.setText("Acquista");
        panel2.add(buttonConfirm, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        radioButtonBuy = new JRadioButton();
        radioButtonBuy.setSelected(true);
        radioButtonBuy.setText("Acquista");
        panel5.add(radioButtonBuy, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radioButtonBook = new JRadioButton();
        radioButtonBook.setText("Prenota");
        panel5.add(radioButtonBook, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Operazione da effettuare?");
        panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Hai un codice sconto?");
        panel7.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        promoCodeField = new JTextField();
        panel7.add(promoCodeField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonPromo = new JButton();
        buttonPromo.setEnabled(false);
        buttonPromo.setText("Applica");
        panel7.add(buttonPromo, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        promoValidLabel = new JLabel();
        promoValidLabel.setText("IsCodeValid?");
        panel6.add(promoValidLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        separator1.setOrientation(1);
        panel3.add(separator1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel3.add(spacer3, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel3.add(spacer4, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPanePassengers = new JScrollPane();
        panel8.add(scrollPanePassengers, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        passengers = new JPanel();
        passengers.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        passengers.setEnabled(true);
        scrollPanePassengers.setViewportView(passengers);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Sommario", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel9.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        routeLabel = new JLabel();
        routeLabel.setText("Da %s a %s");
        panel10.add(routeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dateLabel = new JLabel();
        dateLabel.setText("Data: %s");
        panel10.add(dateLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passengersNumberLabel = new JLabel();
        passengersNumberLabel.setText("Numero passeggeri: %d");
        panel10.add(passengersNumberLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        totalLabel = new JLabel();
        totalLabel.setText("Totale: %.2f€");
        panel10.add(totalLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        promoLabel = new JLabel();
        promoLabel.setText("Promozione applicata: %s");
        panel10.add(promoLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonBuy);
        buttonGroup.add(radioButtonBook);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

    private class PassengerData extends JPanel {

        private static final int GAP_BETWEEN_ELEMENTS = 10;
        private static final int MARGIN = 3;

        private final JTextField name;
        private final JTextField surname;
        private final JCheckBox businessClass;

        PassengerData(int id) {

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            name = new JTextField();
            name.setMaximumSize(new Dimension(Integer.MAX_VALUE, name.getPreferredSize().height));
            name.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    buttonConfirm.setEnabled(canEnableConfirm());
                }
            });

            surname = new JTextField();
            surname.setMaximumSize(new Dimension(Integer.MAX_VALUE, surname.getPreferredSize().height));
            surname.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    buttonConfirm.setEnabled(canEnableConfirm());
                }
            });

            businessClass = new JCheckBox("Business Class");
            businessClass.setSelected(false);
            businessClass.addActionListener(actionEvent -> onBusinessClassChange());


            Border line = BorderFactory.createLineBorder(Color.GRAY);
            Border titled = BorderFactory.createTitledBorder(line, String.format("Passegero %d", id));
            Border margin = BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN);
            setBorder(BorderFactory.createCompoundBorder(margin, titled));

            add(Box.createHorizontalStrut(GAP_BETWEEN_ELEMENTS));
            add(new JLabel("Nome"));
            add(Box.createHorizontalStrut(GAP_BETWEEN_ELEMENTS));
            add(name);
            add(Box.createHorizontalStrut(GAP_BETWEEN_ELEMENTS));
            add(new JLabel("Cognome"));
            add(Box.createHorizontalStrut(GAP_BETWEEN_ELEMENTS));
            add(surname);
            add(Box.createHorizontalStrut(GAP_BETWEEN_ELEMENTS));
            add(businessClass);
            add(Box.createHorizontalStrut(GAP_BETWEEN_ELEMENTS));
        }

        String getPassengerName() {
            return name.getText();
        }

        String getPassengerSurname() {
            return surname.getText();
        }

        boolean isBusinessClass() {
            return businessClass.isSelected();
        }

        boolean areFieldsValid() {
            return !getPassengerName().isBlank() & !getPassengerSurname().isBlank();
        }

        void onBusinessClassChange() {
            if (isBusinessClass()) {
                client.decrementCurrentEconomyPassengers();
                client.incrementCurrentBusinessPassengers();
            } else {
                client.decrementCurrentBusinessPassengers();
                client.incrementCurrentEconomyPassengers();
            }
        }
    }
}
