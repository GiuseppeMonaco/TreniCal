package it.trenical.server.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.trenical.common.Train;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import static it.trenical.common.gui.Util.formatLabel;

public class DeleteTrainDialog extends DeleteDialog<Train> {

    private JPanel main;
    private JLabel idLabel;
    private JLabel typeLabel;
    private JLabel economyCapLabel;
    private JLabel businessCapLabel;

    public DeleteTrainDialog(Train item) {
        super(item);
        addToInfoPane(main);
        formatLabel(idLabel, item.getId());
        formatLabel(typeLabel, item.getType().getName());
        formatLabel(economyCapLabel, item.getEconomyCapacity());
        formatLabel(businessCapLabel, item.getBusinessCapacity());

    }

    @Override
    void deleteItem() {
        server.deleteTrain(itemToDelete);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        main = new JPanel();
        main.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        main.setBorder(BorderFactory.createTitledBorder(null, "Treno", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, null));
        idLabel = new JLabel();
        idLabel.setText("ID: %s");
        main.add(idLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typeLabel = new JLabel();
        typeLabel.setText("Tipo: %s");
        main.add(typeLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        economyCapLabel = new JLabel();
        economyCapLabel.setText("Capacità Economy: %d");
        main.add(economyCapLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        businessCapLabel = new JLabel();
        businessCapLabel.setText("Capacità Business: %d\n");
        main.add(businessCapLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

}
