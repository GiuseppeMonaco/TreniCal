package it.trenical.server.db;

import java.sql.*;
import java.util.logging.Logger;

public class SQLiteConnection implements DatabaseConnection {

    // Singleton class
    private static SQLiteConnection instance;

    private static final Logger logger = Logger.getLogger(SQLiteConnection.class.getName());

    private static final String DATABASE_PATH = "./database.db";

    private Connection connection;
    private Statement statement;

    private SQLiteConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
            statement = connection.createStatement();

            // Per abilitare il controllo delle chiavi esterne nelle tabelle
            statement.execute("PRAGMA foreign_keys = ON;");

            // Si inizializzano tutte le tabelle necessarie
            SQLiteUser.initTable(statement);
            SQLiteFidelityUser.initTable(statement);
            SQLitePromotion.initTable(statement);
            SQLiteTrainType.initTable(statement);
            SQLiteTrain.initTable(statement);
            SQLiteStation.initTable(statement);
            SQLiteRoute.initTable(statement);
            SQLiteTrip.initTable(statement);
            SQLiteTicket.initTable(statement);
            SQLitePaidTicket.initTable(statement);

        } catch (SQLException e) {
            logger.severe("Cannot initialize database connection. Please contact software developer.");
            System.exit(-1);
        }
    }

    public static synchronized SQLiteConnection getInstance() {
        if (instance == null) instance = new SQLiteConnection();
        return instance;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public static void main(String[] args) {
        SQLiteConnection db = SQLiteConnection.getInstance();

        try {
            new SQLiteUser("mario.rossi@gmail.com", "passwordbella123").insertRecord(db);
            new SQLiteFidelityUser("mario.rossi@gmail.com").insertRecord(db);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        try (ResultSet rs = db.statement.executeQuery("SELECT * FROM Users, FidelityUsers F WHERE Users.email = F.userEmail")) {

            while (rs.next()) {
                System.out.print("Email: " + rs.getString("email"));
                System.out.println(" --- Psw: " + rs.getString("password"));
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
}
