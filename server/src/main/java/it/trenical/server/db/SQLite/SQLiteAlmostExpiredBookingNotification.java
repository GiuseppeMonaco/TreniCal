package it.trenical.server.db.SQLite;

import it.trenical.common.Ticket;
import it.trenical.common.User;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;

public record SQLiteAlmostExpiredBookingNotification(User user, Ticket book, long timestamp) implements SQLiteTable<SQLiteAlmostExpiredBookingNotification> {

    static final String TABLE_NAME = "AlmostExpiredBookingNotifications";
    static final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS = """
            ticket INTEGER NOT NULL,
            timestamp INTEGER NOT NULL,
            PRIMARY KEY (ticket),
            FOREIGN KEY (ticket) REFERENCES Tickets(id) ON DELETE CASCADE
            """;

    static final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY = String.format("""
                    SELECT
                      nt.timestamp,
                      -- Ticket principale
                      tk.id,
                      tk.userEmail,
                      u.password                           AS user_password,
                      tk.name                              AS passenger_name,
                      tk.surname                           AS passenger_surname,
                      tk.price                             AS ticket_price,
                      tk.promotion                         AS promotion_code,
                      tk.isBusiness                        AS is_business,
                      -- Fidelity User
                      CASE WHEN (
                        SELECT uf.userEmail
                        FROM %s uf
                        WHERE uf.userEmail = tk.userEmail
                      ) IS NULL THEN 0 ELSE 1 END          AS user_is_fidelity,
                      -- Paid Ticket
                      CASE WHEN (
                        SELECT pt.id
                        FROM %s pt
                        WHERE pt.id = tk.id
                      ) IS NULL THEN 0 ELSE 1 END           AS ticket_is_paid,
                      -- Dati promozione (NULL se non esiste)
                      (
                        SELECT p.name
                        FROM %s p
                        WHERE p.code = tk.promotion
                      )                                     AS promotion_name,
                      (
                        SELECT p.description
                        FROM %s p
                        WHERE p.code = tk.promotion
                      )                                     AS promotion_description,
                      (
                        SELECT p.onlyFidelity
                        FROM %s p
                        WHERE p.code = tk.promotion
                      )                                     AS promotion_onlyFidelity,
                      (
                        SELECT p.discount
                        FROM %s p
                        WHERE p.code = tk.promotion
                      )                                     AS promotion_discount,
                      -- Riferimento al viaggio
                      tk.tripTrain                         AS trip_train_id,
                      tk.tripDepartureTime                 AS trip_departureTime,
                      -- Dati del treno e del suo tipo
                      t.type                               AS train_type,
                      tt.price                             AS trainType_price,
                      t.economyCapacity                    AS train_economyCapacity,
                      t.businessCapacity                   AS train_businessCapacity,
                      -- Disponibilità posti sul viaggio
                      trp.availableEconomySeats            AS trip_availableEconomySeats,
                      trp.availableBusinessSeats           AS trip_availableBusinessSeats,
                      -- Stazioni di partenza
                      trp.departureStation                 AS departureStation_name,
                      s1.address                           AS departure_address,
                      s1.town                              AS departure_town,
                      s1.province                          AS departure_province,
                      -- Stazioni di arrivo
                      trp.arrivalStation                   AS arrivalStation_name,
                      s2.address                           AS arrival_address,
                      s2.town                              AS arrival_town,
                      s2.province                          AS arrival_province,
                      -- Distanza della tratta
                      r.distance                           AS route_distance
                    FROM
                      %s nt,
                      %s tk,
                      %s u,
                      %s trp,
                      %s t,
                      %s tt,
                      %s s1,
                      %s s2,
                      %s r
                    WHERE
                      nt.ticket = tk.id AND
                      -- Utente
                      tk.userEmail                = u.email
                      -- Viaggio
                      AND tk.tripTrain            = trp.train
                      AND tk.tripDepartureTime    = trp.departureTime
                      AND tk.tripDepartureStation = trp.departureStation
                      AND tk.tripArrivalStation   = trp.arrivalStation
                      -- Treno e tipo
                      AND trp.train               = t.id
                      AND t.type                  = tt.name
                      -- Stazioni
                      AND trp.departureStation    = s1.name
                      AND trp.arrivalStation      = s2.name
                      -- Route per distanza
                      AND trp.departureStation    = r.departureStation
                      AND trp.arrivalStation      = r.arrivalStation
            """,
            SQLiteFidelityUser.TABLE_NAME,
            SQLitePaidTicket.TABLE_NAME,
            SQLitePromotion.TABLE_NAME,
            SQLitePromotion.TABLE_NAME,
            SQLitePromotion.TABLE_NAME,
            SQLitePromotion.TABLE_NAME,
            TABLE_NAME,
            SQLiteTicket.TABLE_NAME,
            SQLiteUser.TABLE_NAME,
            SQLiteTrip.TABLE_NAME,
            SQLiteTrain.TABLE_NAME,
            SQLiteTrainType.TABLE_NAME,
            SQLiteStation.TABLE_NAME,
            SQLiteStation.TABLE_NAME,
            SQLiteRoute.TABLE_NAME
    );

    static final String DELETE_QUERY = String.format("""
            DELETE FROM %s AS t
            WHERE EXISTS (
                SELECT 1
                FROM %s tt
                WHERE t.ticket = tt.id
                AND tt.userEmail=?
            );
            """,
            TABLE_NAME,
            SQLiteTicket.TABLE_NAME
    );

    static private final String GET_QUERY = String.format("""
            %s AND tk.id=?;
            """,
            ALL_QUERY
    );

    static private final String GET_SIMILAR_QUERY = String.format("""
            %s AND tk.userEmail=?;
            """,
            ALL_QUERY
    );

    public static void initTable(Statement statement) throws SQLException {
        SQLiteTable.initTable(statement, TABLE_NAME, COLUMNS);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public int getColumnsNumber() {
        return COLUMNS_NUMBER;
    }

    @Override
    public String getInsertQuery() {
        return INSERT_QUERY;
    }

    @Override
    public String getAllQuery() {
        return ALL_QUERY;
    }


    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(INSERT_QUERY);
        st.setInt(1,book.getId());
        st.setLong(2,timestamp);
        st.executeUpdate();
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1,user.getEmail());
        st.executeUpdate();
    }

    public Collection<SQLiteAlmostExpiredBookingNotification> getSimilarRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_SIMILAR_QUERY);
        st.setString(1,user.getEmail());
        ResultSet rs = st.executeQuery();

        Collection<SQLiteAlmostExpiredBookingNotification> ret = new LinkedList<>();
        while (rs.next()) ret.add(toRecord(rs));
        return ret;
    }

    @Override
    public SQLiteAlmostExpiredBookingNotification getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setInt(1,book.getId());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return toRecord(rs);
    }

    @Override
    public SQLiteAlmostExpiredBookingNotification toRecord(ResultSet rs) throws SQLException {
        Ticket t = new SQLiteTicket(null).toRecord(rs);
        return new SQLiteAlmostExpiredBookingNotification(t.getUser(), t, rs.getLong("timestamp"));
    }
}
