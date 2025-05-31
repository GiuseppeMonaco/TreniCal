package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.Login;
import it.trenical.client.observer.Logout;
import it.trenical.client.request.exceptions.InvalidTicketException;
import it.trenical.client.request.exceptions.NoChangeException;
import it.trenical.common.Promotion;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.common.gui.GenericConfirmDialog;
import it.trenical.common.gui.GenericOKDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

public class MainFrame extends JFrame implements Login.Observer, Logout.Observer {

    // Singleton class
    private static MainFrame instance;

    private final Client client;

    private JButton loginButton;
    private JLabel authLabel;
    private JPanel mainPanel;
    private JPanel centerPanel;
    private JButton buttonCustomerArea;

    private CustomerAreaFrame customerAreaFrame;

    private JPanel explorePanel;
    private JPanel tripsPanel;
    private CheckoutPanel checkoutPanel;

    private MainFrame() {
        client = Client.getInstance();
        client.loginSub.attach(this);
        client.logoutSub.attach(this);

        setContentPane(mainPanel);
        setTitle("TreniCal");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        loginButton.addActionListener(actionEvent -> onLoginButton());

        buttonCustomerArea.setVisible(canShowCustomerAreaButton());
        buttonCustomerArea.addActionListener(actionEvent -> onCustomerAreaButton());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] options = {"No", "Sì"};
                int conferma = JOptionPane.showOptionDialog(
                        MainFrame.this,
                        "Sei sicuro di voler chiudere?",
                        "TreniCal",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                if (conferma == 1) {
                    if (client.isAuthenticated()) {
                        try {
                            client.logout();
                        } catch (UnreachableServer ignored) {}
                    }
                    dispose();
                }
            }
        });
    }

    Client getClient() {
        return client;
    }

    private void onLoginButton() {
        if (client.isAuthenticated()) logout();
        else loginDialog();
    }

    private void onCustomerAreaButton() {
        if (!canShowCustomerAreaButton()) return;
        initCustomerAreaFrame();
        customerAreaFrame.display(this);
    }

    private boolean canShowCustomerAreaButton() {
        return client.getCurrentUser() != null;
    }

    public static synchronized MainFrame getInstance() {
        if (instance == null) instance = new MainFrame();
        return instance;
    }

    private void init() {
        initCustomerAreaFrame();
        initTripsPanel();
        initCheckoutPanel();
        showExplorePanel();
        try {
            client.queryStations();
            client.queryTrainTypes();
            client.queryTrips();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    private void display() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateCenterPanel(Component next) {
        if (centerPanel.getComponentCount() > 0) centerPanel.removeAll();
        centerPanel.add(next);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    void initExplorePanel() {
        if (explorePanel == null) explorePanel = new ExplorePanel().getPanel();
    }

    void initCustomerAreaFrame() {
        if (customerAreaFrame == null) customerAreaFrame = new CustomerAreaFrame();
    }

    void initTripsPanel() {
        if (tripsPanel == null) tripsPanel = new TripsPanel().getPanel();
    }

    void initCheckoutPanel() {
        if (checkoutPanel == null) checkoutPanel = new CheckoutPanel();
    }

    void showExplorePanel() {
        initExplorePanel();
        updateCenterPanel(explorePanel);
        pack();
    }

    void showTripsPanel() {
        initTripsPanel();
        updateCenterPanel(tripsPanel);
        pack();
    }

    void showCheckoutPanel() {
        initCheckoutPanel();
        updateCenterPanel(checkoutPanel.getPanel());
        pack();
    }

    private void showDialog(JDialog dialog) {
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    void loginDialog() {
        showDialog(new LoginDialog());
    }

    void signupDialog() {
        showDialog(new SignupDialog());
    }

    void invalidLoginDialog() {
        showDialog(new InvalidLoginDialog());
    }

    void invalidSignupDialog() {
        showDialog(new InvalidSignupDialog());
    }

    void unreachableServerDialog() {
        showDialog(new UnreachableServerDialog());
    }

    void payBookedTicketDialog(Ticket ticket) {
        showDialog(new BuyBookedTicketDialog(ticket));
    }

    void editTicketDialog(Ticket ticket) {
        showDialog(new EditTicketDialog(ticket));
    }

    void genericOKDialog(String message) {
        showDialog(new GenericOKDialog(message));
    }

    @Override
    public void updateOnLogin() {
        authLabel.setText("Ciao " + client.getCurrentUser().getEmail());
        loginButton.setText("Logout");
        buttonCustomerArea.setVisible(canShowCustomerAreaButton());
        queryTickets();
    }

    @Override
    public void updateOnLogout() {
        authLabel.setText("Utente non autenticato");
        loginButton.setText("Login");
        buttonCustomerArea.setVisible(canShowCustomerAreaButton());
        customerAreaFrame.close();
    }

    void login(User user) {
        try {
            client.login(user);
        } catch (InvalidCredentialsException e) {
            invalidLoginDialog();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void logout() {
        try {
            client.logout();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void signup(User user) {
        try {
            client.signup(user);
        } catch (InvalidCredentialsException | UserAlreadyExistsException e) {
            invalidSignupDialog();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryStations() {
        try {
            client.queryStations();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTrips() {
        try {
            client.queryTrips();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTrips(Trip trip) {
        try {
            client.queryTrips(trip);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTrainTypes() {
        try {
            client.queryTrainTypes();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTickets() {
        try {
            client.queryTickets();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            throw new RuntimeException(e); // TODO sistemare questo sistema
        }
    }

    Promotion queryPromotion(Promotion promotion) {
        Promotion ret = null;
        try {
            ret = client.queryPromotion(promotion);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            throw new RuntimeException(e); // TODO sistemare questo sistema
        }
        return ret;
    }

    void setCurrentPromotion(Promotion promotion) {
        try {
            client.setCurrentPromotion(promotion);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void buyTickets(Collection<Ticket> tickets) {
        try {
            client.buyTickets(tickets);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            throw new RuntimeException(e);
        }
    }

    void bookTickets(Collection<Ticket> tickets) {
        try {
            client.bookTickets(tickets);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            throw new RuntimeException(e);
        }
    }

    void payBookedTickets(Collection<Ticket> tickets) {
        try {
            client.payBookedTickets(tickets);
            genericOKDialog("Biglietto acquistato con successo!");
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException | InvalidTicketException e) {
            throw new RuntimeException(e);
        }
    }

    void editTicket(Ticket ticket) {
        try {
            client.editTicket(ticket);
            genericOKDialog("Biglietto modificato con successo!");
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException | InvalidTicketException | NoChangeException e) {
            throw new RuntimeException(e);
        }
    }
    void becomeFidelity() {
        try {
            if(!GenericConfirmDialog.showDialog(this,"""
                    <html><div style='text-align: center;'>
                    Ottieni tantissimi vantaggi diventando un utente fedele!<br>
                    Il programma FedeltàTreno ha un costo di 9,99€ al mese.<br>
                    Cliccando su conferma accetti di attivare l'abbonamento.
                    </div></html>
                    """)) return;
            client.becomeFidelity();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException | NoChangeException e) {
            throw new RuntimeException(e);
        }
    }
    void cancelFidelity() {
        try {
            if(!GenericConfirmDialog.showDialog(this,"""
                    <html><div style='text-align: center;'>
                    Attenzione, ti stai disiscrivendo dal programma FedeltàTreno.<br>
                    Così facendo perderai tutti gli incredibili ed esclusivi vantaggi.<br>
                    Cliccando su conferma la sottoscrizione cesserà con effetto immediato.<br>
                    Potrai iscriverti nuovamente appena vorrai!
                    </div></html>
                    """)) return;
            client.cancelFidelity();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException | NoChangeException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MainFrame m = MainFrame.getInstance();
        m.display();
        m.init();
    }

}
