package it.trenical.client.gui;

import javax.swing.*;
import java.awt.event.*;

public class InvalidSignupDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonRetry;
    private JButton buttonCancel;
    private JButton buttonLogin;

    public InvalidSignupDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonRetry);
        setTitle("TreniCal Login");

        buttonRetry.addActionListener(e -> onRetry());

        buttonCancel.addActionListener(e -> onCancel());

        buttonLogin.addActionListener(actionEvent -> onLogin());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        buttonLogin.addActionListener(actionEvent -> {

        });
    }

    private void onRetry() {
        dispose();
        MainFrame.getInstance().signupDialog();
    }

    private void onCancel() {
        dispose();
    }

    private void onLogin() {
        dispose();
        MainFrame.getInstance().loginDialog();
    }
}
