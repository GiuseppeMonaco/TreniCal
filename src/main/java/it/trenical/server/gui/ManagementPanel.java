package it.trenical.server.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;

abstract class ManagementPanel<T> {
    private JPanel main;
    private JButton buttonCreate;
    private JButton buttonDelete;
    private JButton buttonEdit;
    private JList<T> itemList;

    ManagementPanel(String itemsName, String itemName) {

        if (main.getBorder() instanceof TitledBorder tb) {
            tb.setTitle(String.format(tb.getTitle(),itemsName));
            buttonCreate.setText(String.format(buttonCreate.getText(),itemName));
            buttonDelete.setText(String.format(buttonDelete.getText(),itemName));
            buttonEdit.setText(String.format(buttonEdit.getText(),itemName));
        }

        buttonCreate.addActionListener(actionEvent -> onButtonCreate());

        buttonDelete.setEnabled(false);
        buttonDelete.addActionListener(actionEvent -> onButtonDelete());

        buttonEdit.setEnabled(false);
        buttonEdit.addActionListener(actionEvent -> onButtonEdit());
    }

    abstract void onButtonEdit();
    abstract void onButtonDelete();
    abstract void onButtonCreate();

    JPanel getPanel() {
        return main;
    }

}
