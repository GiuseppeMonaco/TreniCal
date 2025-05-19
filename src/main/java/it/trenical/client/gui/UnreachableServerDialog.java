package it.trenical.client.gui;

import javax.swing.*;

public class UnreachableServerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;

    public UnreachableServerDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
    }

    private void onOK() {
        dispose();
    }
}
