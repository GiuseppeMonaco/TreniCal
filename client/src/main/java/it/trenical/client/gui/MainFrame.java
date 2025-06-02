package it.trenical.client.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.observer.Login;
import it.trenical.client.observer.Logout;
import it.trenical.client.request.exceptions.InvalidSeatsNumberException;
import it.trenical.client.request.exceptions.InvalidTicketException;
import it.trenical.client.request.exceptions.NoChangeException;
import it.trenical.common.Promotion;
import it.trenical.common.Ticket;
import it.trenical.common.Trip;
import it.trenical.common.User;
import it.trenical.common.gui.GenericConfirmDialog;
import it.trenical.common.gui.GenericOKDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

public class MainFrame extends JFrame implements Login.Observer, Logout.Observer {

    // Singleton class
    private static MainFrame instance;

    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

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
                        } catch (UnreachableServer ignored) {
                        }
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

    private void showDialog(JDialog dialog, Component parent) {
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
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

    void unreachableServerDialog(Component parent) {
        showDialog(new UnreachableServerDialog(), parent);
    }

    public void invalidTokenDialog() {
        invalidTokenDialog(this);
    }

    public void invalidTokenDialog(Component parent) {
        GenericOKDialog.showDialog(parent, """
                <html><div style='text-align: center; width: 300px;'>
                La sessione non è più valida.<br>
                Perfavore esegui nuovamente il login.
                </div></html>
                """);
    }

    public void invalidSeatsNumberDialog() {
        invalidSeatsNumberDialog(this);
    }

    public void invalidSeatsNumberDialog(Component parent) {
        GenericOKDialog.showDialog(parent, """
                <html><div style='text-align: center; width: 300px;'>
                Il numero di posti selezionato non è più disponibile.<br>
                Perfavore esegui una nuova ricerca.
                </div></html>
                """);
    }

    void payBookedTicketDialog(Ticket ticket) {
        showDialog(new BuyBookedTicketDialog(ticket), customerAreaFrame);
    }

    void editTicketDialog(Ticket ticket) {
        showDialog(new EditTicketDialog(ticket), customerAreaFrame);
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
            invalidTokenDialog();
        }
    }

    Promotion queryPromotion(Promotion promotion) {
        Promotion ret = null;
        try {
            ret = client.queryPromotion(promotion);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            invalidTokenDialog();
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
            invalidTokenDialog();
        } catch (InvalidSeatsNumberException e) {
            invalidSeatsNumberDialog();
        }
    }

    void bookTickets(Collection<Ticket> tickets) {
        try {
            client.bookTickets(tickets);
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            invalidTokenDialog();
        } catch (InvalidSeatsNumberException e) {
            invalidSeatsNumberDialog();
        }
    }

    void payBookedTickets(Collection<Ticket> tickets, Component dialogsParent) {
        try {
            client.payBookedTickets(tickets);
            GenericOKDialog.showDialog(dialogsParent, "Biglietto acquistato con successo!");
        } catch (UnreachableServer e) {
            unreachableServerDialog(customerAreaFrame);
        } catch (InvalidSessionTokenException e) {
            invalidTokenDialog();
        } catch (InvalidTicketException e) {
            logger.error(e.getMessage());
        }
    }

    void editTicket(Ticket ticket, Component dialogsParent) {
        try {
            client.editTicket(ticket);
            GenericOKDialog.showDialog(dialogsParent, "Biglietto modificato con successo!");
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        } catch (InvalidSessionTokenException e) {
            invalidTokenDialog();
        } catch (InvalidSeatsNumberException e) {
            invalidSeatsNumberDialog(customerAreaFrame);
        } catch (InvalidTicketException | NoChangeException e) {
            logger.error(e.getMessage());
        }
    }

    void becomeFidelity() {
        try {
            if (!GenericConfirmDialog.showDialog(customerAreaFrame, """
                    <html><div style='text-align: center;'>
                    Ottieni tantissimi vantaggi diventando un utente fedele!<br>
                    Il programma FedeltàTreno ha un costo di 9,99€ al mese.<br>
                    Cliccando su conferma accetti di attivare l'abbonamento.
                    </div></html>
                    """)) return;
            client.becomeFidelity();
        } catch (UnreachableServer e) {
            unreachableServerDialog(customerAreaFrame);
        } catch (InvalidSessionTokenException e) {
            invalidTokenDialog();
        } catch (NoChangeException e) {
            logger.error(e.getMessage());
        }
    }

    void cancelFidelity() {
        try {
            if (!GenericConfirmDialog.showDialog(customerAreaFrame, """
                    <html><div style='text-align: center;'>
                    Attenzione, ti stai disiscrivendo dal programma FedeltàTreno.<br>
                    Così facendo perderai tutti gli incredibili ed esclusivi vantaggi.<br>
                    Cliccando su conferma la sottoscrizione cesserà con effetto immediato.<br>
                    Potrai iscriverti nuovamente appena vorrai!
                    </div></html>
                    """)) return;
            client.cancelFidelity();
        } catch (UnreachableServer e) {
            unreachableServerDialog(customerAreaFrame);
        } catch (InvalidSessionTokenException e) {
            invalidTokenDialog();
        } catch (NoChangeException e) {
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        MainFrame m = MainFrame.getInstance();
        m.display();
        m.init();
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        loginButton = new JButton();
        loginButton.setEnabled(true);
        loginButton.setHideActionText(false);
        loginButton.setText("Login");
        panel1.add(loginButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, 35), null, 0, false));
        authLabel = new JLabel();
        authLabel.setText("Utente non autenticato");
        panel1.add(authLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonCustomerArea = new JButton();
        buttonCustomerArea.setText("Area Personale");
        panel1.add(buttonCustomerArea, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 35), null, 0, false));
        centerPanel = new JPanel();
        centerPanel.setLayout(new CardLayout(0, 0));
        mainPanel.add(centerPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(533, 300), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
