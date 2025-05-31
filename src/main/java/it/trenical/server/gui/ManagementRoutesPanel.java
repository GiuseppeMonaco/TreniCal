package it.trenical.server.gui;

import it.trenical.common.Route;
import it.trenical.server.observer.RoutesCache;

public class ManagementRoutesPanel extends ManagementPanel<Route> implements RoutesCache.Observer {

    ManagementRoutesPanel() {
        super("Tratte", "Tratta");
        server.routesCacheObs.attach(this);
    }

    @Override
    void createDialog() {
        new CreateRouteDialog().showDialog();
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editRoute");
    }

    @Override
    void deleteDialog() {
        new DeleteRouteDialog(itemsList.getSelectedValue()).showDialog();
    }

    @Override
    public void updateRoutesCache() {
        updateItemsList(server.getRoutesCache());
    }
}
