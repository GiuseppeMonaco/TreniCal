package it.trenical.common.gui;

import javax.swing.*;

public class Util {

    public static void formatLabel(JLabel label, Object... obj) {
        label.setText(String.format(label.getText(), obj));
    }

}
