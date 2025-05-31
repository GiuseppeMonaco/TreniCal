package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.*;
import it.trenical.common.*;
import it.trenical.common.gui.GenericOKDialog;

import javax.swing.*;
import javax.swing.border.Border;
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
        CurrentPrice.Observer
{
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

        passengers.setLayout(new BoxLayout(passengers,BoxLayout.Y_AXIS));
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
        if(!canButtonPromoBeEnabled()) return;
        if(!client.isAuthenticated()) {
            mainFrame.loginDialog();
            return;
        }
        mainFrame.setCurrentPromotion(PromotionData.newBuilder(promoCodeField.getText()).build());
    }

    private boolean canButtonPromoBeEnabled() {
        return !promoCodeField.getText().isBlank();
    }

    private void onRadioButtonBuy() {
        if(!radioButtonBuy.isSelected()) return;
        buttonConfirm.setText(BUY_LABEL);
    }

    private void onRadioButtonBook() {
        if(!radioButtonBook.isSelected()) return;
        buttonConfirm.setText(BOOK_LABEL);
    }

    void onButtonBack() {
        mainFrame.showTripsPanel();
    }

    void onButtonConfirm() {
        if(!canEnableConfirm()) return;
        if(!client.isAuthenticated()) {
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
            throw new RuntimeException(e);
        }
        promoValidLabel.setVisible(false);

        GenericOKDialog.showDialog(mainFrame,dialogMessage);
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
        passengersNumberLabel.setText(String.format(passengersNumberLabelDefault,passNum));
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
        if(trip != null) {
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
        totalLabel.setText(String.format(totalLabelDefault,client.getCurrentTotalPrice()));
    }

    @Override
    public void updateOnCurrentPromotion() {
        Promotion promo = client.getCurrentPromotion();

        String promoString = "Nessuna";
        String promoValidString = "Promozione non valida!";
        Color promoValidColor = Color.RED;
        if(promo != null) {
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
            setBorder(BorderFactory.createCompoundBorder(margin,titled));

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
            if(isBusinessClass()) {
                client.decrementCurrentEconomyPassengers();
                client.incrementCurrentBusinessPassengers();
            } else {
                client.decrementCurrentBusinessPassengers();
                client.incrementCurrentEconomyPassengers();
            }
        }
    }
}
