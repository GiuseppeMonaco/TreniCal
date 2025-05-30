package it.trenical.server.gui;

import it.trenical.server.Server;
import it.trenical.server.connection.GrpcServerConnection;
import it.trenical.server.db.SQLite.SQLiteConnection;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Singleton class
public class AdminMainFrame extends JFrame {

    private static AdminMainFrame INSTANCE;

    private Server server;

    private JPanel mainPanel;
    private JPanel managementPanel;

    private final JPanel trainTypesManagementPanel;
    //private final JPanel trainsManagementPanel; // TODO
    //private final JPanel stationsManagementPanel;
    //private final JPanel routesManagementPanel;
    //private final JPanel tripsManagementPanel;
    //private final JPanel promotionsManagementPanel;

    //private final JPanel ticketsManagementPanel;

    private AdminMainFrame() {

        server = Server.INSTANCE;
        server.initDatabase(SQLiteConnection.getInstance());
        server.initServerConnection(GrpcServerConnection.getInstance());

        setContentPane(mainPanel);
        setTitle("TreniCal Admin");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        trainTypesManagementPanel = new TrainTypesManagementPanel().getPanel();
        //trainsManagementPanel = new TrainsManagementPanel().getPanel(); // TODO
        //stationsManagementPanel = new StationsManagementPanel().getPanel();
        //routesManagementPanel = new RoutesManagementPanel().getPanel();
        //tripsManagementPanel = new TripsManagementPanel().getPanel();
        //promotionsManagementPanel = new PromotionsManagementPanel().getPanel();

        managementPanel.setLayout(new BoxLayout(managementPanel, BoxLayout.X_AXIS));
        managementPanel.add(trainTypesManagementPanel);
        //managementPanel.add(trainsManagementPanel); // TODO
        //managementPanel.add(stationsManagementPanel);
        //managementPanel.add(routesManagementPanel);
        //managementPanel.add(tripsManagementPanel);
        //managementPanel.add(promotionsManagementPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int conferma = JOptionPane.showConfirmDialog(
                        AdminMainFrame.this,
                        "Sei sicuro di voler chiudere il server?",
                        "TreniCal Admin",
                        JOptionPane.YES_NO_OPTION
                );
                if (conferma == JOptionPane.YES_OPTION) {
                    if (server != null) {
                        server.closeDatabase();
                    }
                    dispose();
                }
            }
        });
    }

    public static synchronized AdminMainFrame getInstance() {
        if (INSTANCE == null) INSTANCE = new AdminMainFrame();
        return INSTANCE;
    }

    private void init() {

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
