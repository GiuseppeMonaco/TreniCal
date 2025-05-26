package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.Login;
import it.trenical.client.observer.Logout;
import it.trenical.client.observer.TripsCache;
import it.trenical.common.Trip;
import it.trenical.common.User;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class MainFrame extends JFrame implements Login.Observer, Logout.Observer, TripsCache.Observer {

    // Singleton class
    private static MainFrame instance;

    private final Client c;

    private JPanel rightPanel;
    private JPanel leftPanel;
    private JButton loginButton;
    private JLabel authLabel;
    private JPanel mainPanel;

    private JPanel customerAreaPanel;
    private JPanel explorePanel;
    private JPanel tripsPanel;

    private MainFrame() {
        c = Client.getInstance();
        c.loginSub.attach(this);
        c.logoutSub.attach(this);
        c.filteredTripsCacheSub.attach(this);

        setContentPane(mainPanel);
        setTitle("TreniCal");

        showExplorePanel();
        leftPanel.setVisible(false);

        loginButton.addActionListener(actionEvent -> onLoginButton());
    }

    private void onLoginButton() {
        if (c.isAuthenticated()) logout();
        else loginDialog();
    }

    public static synchronized MainFrame getInstance() {
        if (instance == null) instance = new MainFrame();
        return instance;
    }

    private void init() {
        initCustomerAreaPanel();
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

    private void updateLeftPanel(Component next) {
        if (leftPanel.getComponentCount() > 0) leftPanel.removeAll();
        leftPanel.add(next);
    }

    private void updateRightPanel(Component next) {
        if (rightPanel.getComponentCount() > 0) rightPanel.removeAll();
        rightPanel.add(next);
    }

    void initExplorePanel() {
        if (explorePanel == null) explorePanel = new ExplorePanel().getPanel();
    }

    void initCustomerAreaPanel() {
        if (customerAreaPanel == null) customerAreaPanel = new CustomerAreaPanel().getPanel();
    }

    void initTripsPanel() {
        if (tripsPanel == null) tripsPanel = new TripsPanel().getPanel();
    }

    void showExplorePanel() {
        initExplorePanel();
        updateRightPanel(explorePanel);
        rightPanel.setVisible(true);
        pack();
    }

    void showCustomerAreaPanel() {
        initCustomerAreaPanel();
        updateLeftPanel(customerAreaPanel);
        leftPanel.setVisible(true);
        queryTickets();
        pack();
    }

    void showTripsPanel() {
        initTripsPanel();
        updateRightPanel(tripsPanel);
        rightPanel.setVisible(true);
        pack();
    }

    void hideLeftPanel() {
        leftPanel.setVisible(false);
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
        showCustomerAreaPanel();
        //pack();
    }

    @Override
    public void updateOnLogout() {
        authLabel.setText("Utente non autenticato");
        loginButton.setText("Login");
        if(leftPanel.getComponentCount() > 0) leftPanel.removeAll();
        leftPanel.setVisible(false);
        hideLeftPanel();
        pack();
    }

    @Override
    public void updateTripsCache(Collection<Trip> cache) {
        showTripsPanel();
        pack();
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
