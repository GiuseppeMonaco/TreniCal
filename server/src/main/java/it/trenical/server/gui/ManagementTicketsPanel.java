package it.trenical.server.gui;

import it.trenical.server.observer.TicketsCache;
import it.trenical.common.Ticket;

public class ManagementTicketsPanel extends ManagementPanel<Ticket> implements TicketsCache.Observer {

    ManagementTicketsPanel() {
        super("Biglietti", "Biglietto");
        server.ticketsCacheObs.attach(this);
        disableCreateButton();
        disableEditButton();
        disableDeleteButton();
    }

    @Override
    void createDialog() {
        throw new UnsupportedOperationException("createTicket");
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editTicket");
    }

    @Override
    void deleteDialog() {
        throw new UnsupportedOperationException("deleteTicket");
    }

    @Override
    public void updateTicketsCache() {
        updateItemsList(server.getTicketsCache());
    }
}
