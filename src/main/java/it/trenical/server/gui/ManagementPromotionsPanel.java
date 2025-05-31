package it.trenical.server.gui;

import it.trenical.common.Promotion;
import it.trenical.server.observer.PromotionsCache;

public class ManagementPromotionsPanel extends ManagementPanel<Promotion> implements PromotionsCache.Observer {

    ManagementPromotionsPanel() {
        super("Promozioni", "Promozione");
        server.promotionsCacheObs.attach(this);
    }

    @Override
    void createDialog() {
        new CreatePromotionDialog().showDialog();
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editPromotion");
    }

    @Override
    void deleteDialog() {
        new DeletePromotionDialog(itemsList.getSelectedValue()).showDialog();
    }

    @Override
    public void updatePromotionsCache() {
        updateItemsList(server.getPromotionsCache());
    }
}
