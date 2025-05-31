package it.trenical.common.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GenericConfirmDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonConfirm;
    private JButton buttonCancel;
    private JLabel messageLabel;

    private boolean isConfirmed = false;

    private GenericConfirmDialog(String message) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonConfirm);

        messageLabel.setText(message.trim());

        buttonConfirm.addActionListener(e -> onConfirm());

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
    }

    private void onConfirm() {
        isConfirmed = true;
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static boolean showDialog(Component parent, String message) {
        GenericConfirmDialog d = new GenericConfirmDialog(message);
        d.pack();
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
        return d.isConfirmed;
    }
}
