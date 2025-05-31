package it.trenical.server.gui;

import it.trenical.common.User;
import it.trenical.server.observer.UsersCache;

public class ManagementUsersPanel extends ManagementPanel<User> implements UsersCache.Observer {

    ManagementUsersPanel() {
        super("Utenti", "Utente");
        server.usersCacheObs.attach(this);
        disableCreateButton();
        disableEditButton();
        disableDeleteButton();
    }

    @Override
    void createDialog() {
        throw new UnsupportedOperationException("createUser");
    }

    @Override
    void editDialog() {
        throw new UnsupportedOperationException("editUser");
    }

    @Override
    void deleteDialog() {
        throw new UnsupportedOperationException("deleteUser");
    }

    @Override
    public void updateUsersCache() {
        updateItemsList(server.getUsersCache());
    }
}
