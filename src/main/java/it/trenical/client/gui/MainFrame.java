package it.trenical.client.gui;

import it.trenical.client.Client;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.User;

import javax.swing.*;

public class MainFrame extends JFrame {

    // Singleton class
    private static MainFrame instance;

    private final Client c = Client.getInstance();

    private JPanel mainPanel;
    private JButton loginButton;
    private JLabel authLabel;

    private MainFrame() {
        setContentPane(mainPanel);
        setTitle("TreniCal");

        loginButton.addActionListener(actionEvent -> {
            if (c.isAuthenticated()) {
                doLogout();
            } else {
                loginDialog();
            }
        });
    }

    public static synchronized MainFrame getInstance() {
        if (instance == null) instance = new MainFrame();
        return instance;
    }

    private void display() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
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

    private void setAuthLabel(User user) {
        if (user == null) {
            authLabel.setText("Utente non autenticato");
        } else {
            authLabel.setText("Ciao " + user.getEmail());
        }
    }

    void doLogin(User user) {
        try {
            c.login(user);
            setAuthLabel(user);
            loginButton.setText("Logout");
        } catch (InvalidCredentialsException e) {
            invalidLoginDialog();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void doLogout() {
        try {
            c.logout();
            setAuthLabel(null);
            loginButton.setText("Login");
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    void doSignup(User user) {
        try {
            c.signup(user);
            setAuthLabel(user);
        } catch (InvalidCredentialsException | UserAlreadyExistsException e) {
            invalidSignupDialog();
        } catch (UnreachableServer e) {
            unreachableServerDialog();
        }
    }

    public static void main(String[] args) {
        MainFrame.getInstance().display();
    }
}
