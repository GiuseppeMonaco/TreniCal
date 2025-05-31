package it.trenical.client.gui;

import javax.swing.*;
import java.awt.event.*;

public class InvalidLoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonRetry;
    private JButton buttonCancel;
    private JButton buttonSignup;

    public InvalidLoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonRetry);
        setTitle("TreniCal Login");

        buttonRetry.addActionListener(e -> onRetry());

        buttonCancel.addActionListener(e -> onCancel());

        buttonSignup.addActionListener(actionEvent -> onSignup());

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

    private void onRetry() {
        dispose();
        MainFrame.getInstance().loginDialog();
    }

    private void onCancel() {
        dispose();
    }

    private void onSignup() {
        dispose();
        MainFrame.getInstance().signupDialog();
    }
}
