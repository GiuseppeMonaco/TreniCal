package it.trenical.server.gui;

import it.trenical.common.Promotion;

import javax.swing.*;

import static it.trenical.common.gui.Util.formatLabel;

public class DeletePromotionDialog extends DeleteDialog<Promotion> {

    private JPanel main;
    private JLabel nameLabel;
    private JLabel codeLabel;
    private JLabel descriptionLabel;
    private JLabel discountLabel;
    private JLabel onlyFidelityLabel;

    public DeletePromotionDialog(Promotion item) {
        super(item);
        addToInfoPane(main);
        formatLabel(codeLabel,item.getCode());
        formatLabel(nameLabel,item.getName());
        formatLabel(descriptionLabel,item.getDescription());
        formatLabel(discountLabel,convertDiscount(item.getDiscount()));
        formatLabel(onlyFidelityLabel,item.isOnlyFidelityUser() ? "Si" : "No");
    }

    private int convertDiscount(float x) {
        return (int) (1 - x) * 100;
    }

    @Override
    void deleteItem() {
        server.deletePromotion(itemToDelete);
    }
}
