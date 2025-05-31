package it.trenical.server.gui;

import it.trenical.server.Server;

import javax.swing.*;
import java.awt.event.*;

abstract class DeleteDialog<T> extends JDialog {
    private JPanel contentPane;
    private JButton buttonDelete;
    private JButton buttonCancel;
    private JPanel infoPanel;

    T itemToDelete;

    AdminMainFrame mainFrame;
    Server server;

    public DeleteDialog(T item) {

        mainFrame = AdminMainFrame.getInstance();
        server = mainFrame.getServer();

        itemToDelete = item;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonDelete);

        buttonDelete.addActionListener(e -> onDelete());

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

    void addToInfoPane(JPanel panel) {
        infoPanel.add(panel);
    }

    private void onDelete() {
        deleteItem();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    void showDialog() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    abstract void deleteItem();
}
