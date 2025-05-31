package it.trenical.server.gui;

import it.trenical.common.PromotionData;
import it.trenical.server.db.exceptions.PrimaryKeyException;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreatePromotionDialog extends CreateDialog {
    private JPanel main;
    private JTextField codeField;
    private JTextField nameField;
    private JTextPane descriptionPane;
    private JCheckBox onlyFidelityCheckBox;
    private JTextField discountField;

    public CreatePromotionDialog() {
        super();
        addToFormPane(main);

        codeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
        discountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buttonCreate.setEnabled(canCreateButtonBeEnabled());
            }
        });
    }

    private boolean isCodeValid() {
        return !codeField.getText().isBlank();
    }

    private boolean isNameValid() {
        return !nameField.getText().isBlank();
    }

    private boolean isDiscountValid() {
        String text = discountField.getText();
        int x = parseInteger(text);
        return !text.isBlank() && x >= 0 && x <= 100;
    }

    private float getDiscount() {
        int x = parseInteger(discountField.getText());
        return 1 - ((float) x / 100);
    }

    @Override
    boolean canCreateButtonBeEnabled() {
        return isCodeValid() &&
                isNameValid() &&
                isDiscountValid();
    }

    @Override
    void createItem() {
        try {
            server.createPromotion(PromotionData.newBuilder(codeField.getText())
                    .setName(nameField.getText())
                    .setDescription(descriptionPane.getText())
                    .setOnlyFidelityUser(onlyFidelityCheckBox.isSelected())
                    .setDiscount(getDiscount())
                    .build()
            );
        } catch (PrimaryKeyException e) {
            mainFrame.violatedPrimaryKeyDialog();
            return;
        }
        itemSuccessfullyAddedDialog();
        dispose();
    }
}
