package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.Login;
import it.trenical.client.observer.Logout;
import it.trenical.common.Trip;
import it.trenical.common.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements Login.Observer, Logout.Observer {

    // Singleton class
    private static MainFrame instance;

    private final Client c;

    private JButton loginButton;
    private JLabel authLabel;
    private JPanel mainPanel;
    private JPanel centerPanel;
    private JButton buttonCustomerArea;

    private CustomerAreaFrame customerAreaFrame;

    private JPanel explorePanel;
    private JPanel tripsPanel;

    private MainFrame() {
        c = Client.getInstance();
        c.loginSub.attach(this);
        c.logoutSub.attach(this);

        setContentPane(mainPanel);
        setTitle("TreniCal");
        showExplorePanel();

        loginButton.addActionListener(actionEvent -> onLoginButton());

        buttonCustomerArea.setVisible(canShowCustomerAreaButton());
        buttonCustomerArea.addActionListener(actionEvent -> onCustomerAreaButton());
    }

    private void onLoginButton() {
        if (c.isAuthenticated()) logout();
        else loginDialog();
    }

    private void onCustomerAreaButton() {
        if (!canShowCustomerAreaButton()) return;
        initCustomerAreaFrame();
        customerAreaFrame.display();
    }

    private boolean canShowCustomerAreaButton() {
        return c.getCurrentUser() != null;
    }

    public static synchronized MainFrame getInstance() {
        if (instance == null) instance = new MainFrame();
        return instance;
    }

    private void init() {
        initCustomerAreaFrame();
        initExplorePanel();
        initTripsPanel();
        try {
            c.queryStations();
            c.queryTrainTypes();
            c.queryTrips();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    private void display() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateCenterPanel(Component next) {
        if (centerPanel.getComponentCount() > 0) centerPanel.removeAll();
        centerPanel.add(next);
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

    private void showDialog(JDialog dialog) {
        dialog.pack();
        dialog.setLocationRelativeTo(null);
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

    @Override
    public void updateOnLogin() {
        authLabel.setText("Ciao " + c.getCurrentUser().getEmail());
        loginButton.setText("Logout");
        buttonCustomerArea.setVisible(canShowCustomerAreaButton());
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
            c.login(user);
        } catch (InvalidCredentialsException e) {
            invalidLoginDialog();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void logout() {
        try {
            c.logout();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void signup(User user) {
        try {
            c.signup(user);
        } catch (InvalidCredentialsException | UserAlreadyExistsException e) {
            invalidSignupDialog();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryStations() {
        try {
            c.queryStations();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTrips() {
        try {
            c.queryTrips();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTrips(Trip trip) {
        try {
            c.queryTrips(trip);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTrainTypes() {
        try {
            c.queryTrainTypes();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void queryTickets() {
        try {
            c.queryTickets();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MainFrame m = MainFrame.getInstance();
        m.display();
        m.init();
    }

}
