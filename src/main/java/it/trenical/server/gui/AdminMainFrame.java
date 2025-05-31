package it.trenical.server.gui;

import it.trenical.common.gui.GenericOKDialog;
import it.trenical.server.Server;
import it.trenical.server.connection.GrpcServerConnection;
import it.trenical.server.db.SQLite.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Singleton class
public class AdminMainFrame extends JFrame {

    private static AdminMainFrame INSTANCE;

    private final Server server;

    private JPanel mainPanel;
    private JPanel managementPanel;

    private JPanel trainTypesManagementPanel;
    private JPanel trainsManagementPanel;
    private JPanel stationsManagementPanel;
    private JPanel routesManagementPanel;
    private JPanel tripsManagementPanel;
    private JPanel promotionsManagementPanel;
    private JPanel usersManagementPanel;
    private JPanel ticketsManagementPanel;

    private AdminMainFrame() {

        server = Server.INSTANCE;
        server.initDatabase(SQLiteConnection.getInstance());
        server.initServerConnection(GrpcServerConnection.getInstance());

        setContentPane(mainPanel);
        setTitle("TreniCal Admin");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] options = {"No", "Sì"};
                int conferma = JOptionPane.showOptionDialog(
                        AdminMainFrame.this,
                        "Sei sicuro di voler chiudere il server?",
                        "TreniCal Admin",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                if (conferma == 1) {
                    server.closeDatabase();
                    dispose();
                }
            }
        });
    }

    public static synchronized AdminMainFrame getInstance() {
        if (INSTANCE == null) INSTANCE = new AdminMainFrame();
        return INSTANCE;
    }

    void violatedPrimaryKeyDialog() {
        showDialog(new GenericOKDialog("""
                <html><div style='text-align: center; width: 300px;'>
                Un elemento con questi identificativi esiste già.<br>
                Perfavore utilizza degli identificativi differenti.
                </div></html>
        """));
    }

    void violatedForeignKeyDialog() {
        showDialog(new GenericOKDialog("""
                <html><div style='text-align: center; width: 300px;'>
                Dei campi esterni non esistono.<br>
                Potrebbero essere stati eliminati.<br>
                Perfavore riprova.
                </div></html>
        """));
    }

    void itemSuccessfullyAddedDialog() {
        showDialog(new GenericOKDialog("Elemento aggiunto con successo!"));
    }

    Server getServer() {
        return server;
    }

    private void showDialog(JDialog dialog) {
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void init() {
        trainTypesManagementPanel = new ManagementTrainTypesPanel().getPanel();
        trainsManagementPanel = new ManagementTrainsPanel().getPanel();
        stationsManagementPanel = new ManagementStationsPanel().getPanel();
        routesManagementPanel = new ManagementRoutesPanel().getPanel();
        tripsManagementPanel = new ManagementTripsPanel().getPanel();
        promotionsManagementPanel = new ManagementPromotionsPanel().getPanel();
        usersManagementPanel = new ManagementUsersPanel().getPanel();
        ticketsManagementPanel = new ManagementTicketsPanel().getPanel();

        managementPanel.setLayout(new BoxLayout(managementPanel, BoxLayout.X_AXIS));
        managementPanel.add(trainTypesManagementPanel);
        managementPanel.add(trainsManagementPanel);
        managementPanel.add(stationsManagementPanel);
        managementPanel.add(routesManagementPanel);
        managementPanel.add(tripsManagementPanel);
        managementPanel.add(promotionsManagementPanel);
        managementPanel.add(usersManagementPanel);
        managementPanel.add(ticketsManagementPanel);

        server.updateTrainTypesCache();
        server.updateTrainsCache();
        server.updateStationsCache();
        server.updateRoutesCache();
        server.updateTripsCache();
        server.updatePromotionsCache();
        server.updateUsersCache();
        server.updateTicketsCache();
    }

    private void display() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        AdminMainFrame m = AdminMainFrame.getInstance();
        m.init();
        m.display();
    }
}
