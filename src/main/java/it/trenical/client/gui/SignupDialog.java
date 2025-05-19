package it.trenical.client.gui;

import it.trenical.common.User;
import it.trenical.common.UserData;

import javax.swing.*;
import java.awt.event.*;

public class SignupDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonSignup;
    private JButton buttonCancel;
    private JTextField emailField;
    private JPasswordField passwordField;

    protected SignupDialog() {
        setContentPane(contentPane);
        setModalityType(DEFAULT_MODALITY_TYPE);
        getRootPane().setDefaultButton(buttonSignup);
        setTitle("TreniCal Login");

        buttonSignup.setEnabled(false);
        buttonSignup.addActionListener(e -> {
            // Ulteriore controllo per evitare che il cliente inserisca qualche campo vuoto
            if (emailField.getText().isBlank() || passwordField.getPassword().length == 0) return;
            onSignup();
        });

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

        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonSignup.setEnabled(canEnableLogin());
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonSignup.setEnabled(canEnableLogin());
            }
        });
    }

    private void onSignup() {
        dispose();
        User user = new UserData(
                emailField.getText().toLowerCase(),
                new String(passwordField.getPassword()
        ));
        MainFrame.getInstance().doSignup(user);
    }

    private void onCancel() {
        dispose();
    }

    private boolean canEnableLogin() {
        return !emailField.getText().isBlank() && passwordField.getPassword().length != 0;
    }
}
