package it.trenical.common.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GenericOKDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel messageLabel;

    private GenericOKDialog(String message) {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        messageLabel.setText(message.trim());

        buttonOK.addActionListener(e -> onButtonOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onButtonOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onButtonOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onButtonOK() {
        dispose();
    }

    public static void showDialog(Component parent, String message) {
        GenericOKDialog d = new GenericOKDialog(message);
        d.pack();
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }
}
