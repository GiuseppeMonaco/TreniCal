package it.trenical.server.db.SQLite;

import it.trenical.common.*;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

public class SQLiteTicket implements SQLiteTable<Ticket>, Ticket {

    static private final String TABLE_NAME = "Tickets";
    static private final int COLUMNS_NUMBER = 11;

    static private final String COLUMNS = """
            id INTEGER NOT NULL,
            userEmail TEXT NOT NULL,
            name TEXT(100) NOT NULL,
            surname TEXT(100) NOT NULL,
            price REAL NOT NULL,
            promotion INTEGER,
            tripTrain INTEGER NOT NULL,
            tripDepartureTime INTEGER NOT NULL,
            tripDepartureStation TEXT NOT NULL,
            tripArrivalStation TEXT NOT NULL,
            isBusiness INTEGER(1) NOT NULL,
            PRIMARY KEY (id),
            FOREIGN KEY (userEmail) REFERENCES Users(email) ON DELETE CASCADE,
            FOREIGN KEY (promotion) REFERENCES Promotions(code) ON DELETE SET NULL,
            FOREIGN KEY (tripTrain,tripDepartureTime,tripDepartureStation,tripArrivalStation)
            REFERENCES Trips(train,departureTime,departureStation,arrivalStation) ON DELETE CASCADE
            """;

    static private final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY = String.format("""
                    SELECT
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
                      %s tk,
                      %s u,
                      %s trp,
                      %s t,
                      %s tt,
                      %s s1,
                      %s s2,
                      %s r
                    WHERE
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
            SQLiteUser.TABLE_NAME,
            SQLiteTrip.TABLE_NAME,
            SQLiteTrain.TABLE_NAME,
            SQLiteTrainType.TABLE_NAME,
            SQLiteStation.TABLE_NAME,
            SQLiteStation.TABLE_NAME,
            SQLiteRoute.TABLE_NAME
            );

    static private final String GET_QUERY = String.format("""
            %s AND
            tk.id=?;
            """,
            ALL_QUERY);

    static private final String SIMILAR_QUERY = String.format("""
            %s AND
            tk.userEmail=?;
            """,
            ALL_QUERY
            );

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s WHERE
            id=?;
            """,
            TABLE_NAME
    );

    static void initTable(Statement statement) throws SQLException {
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

    private final Ticket data;

    public SQLiteTicket(Ticket data) {
        this.data = data;
    }

    public SQLiteTicket(int id, String userEmail) {
        this.data = TicketData.newBuilder(id)
                .setUser(new UserData(userEmail))
                .build();
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        db.atomicTransaction(() -> {
            PreparedStatement st = c.prepareStatement(INSERT_QUERY);
            if(getId() > -1) st.setInt(1, getId());
            st.setString(2, getUser().getEmail());
            st.setString(3, getName());
            st.setString(4, getSurname());
            st.setFloat(5, getPrice());
            if(getPromotion() != null) st.setString(6,getPromotion().getCode());
            st.setInt(7,getTrip().getTrain().getId());
            st.setLong(8,getTrip().getDepartureTime().getTimeInMillis());
            st.setString(9,getTrip().getRoute().getDepartureStation().getName());
            st.setString(10,getTrip().getRoute().getArrivalStation().getName());
            st.setBoolean(COLUMNS_NUMBER,isBusiness());
            st.executeUpdate();

            if(isPaid()) {
                ResultSet rs = st.getGeneratedKeys();
                rs.next();
                new SQLitePaidTicket(rs.getInt(1)).insertRecord(db);
            }
        });
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        StringBuilder sb = new StringBuilder(String.format("UPDATE %s SET ",TABLE_NAME));
        if(getUser() != null && getUser().getEmail() != null)
            sb.append(String.format("userEmail='%s', ",getUser().getEmail()));
        if(getName() != null) sb.append(String.format("name='%s', ",getName()));
        if(getSurname() != null) sb.append(String.format("surname='%s', ",getSurname()));
        sb.append(String.format("price=%s, ",getPrice()));
        if(getPromotion() != null) sb.append(String.format("promotion='%s', ",getPromotion()));

        Trip t = getTrip();
        if (t != null) {
            Route r = t.getRoute();
            if (t.getTrain() != null &&
                    t.getDepartureTime() != null &&
                    r != null &&
                    r.getDepartureStation() != null &&
                    r.getArrivalStation() != null) {
                sb.append(String.format("tripTrain=%d, ",t.getTrain().getId()));
                sb.append(String.format("tripDepartureTime=%d, ",t.getDepartureTime().getTimeInMillis()));
                sb.append(String.format("tripDepartureStation='%s', ",r.getDepartureStation().getName()));
                sb.append(String.format("tripArrivalStation='%s', ",r.getArrivalStation().getName()));
            }
        }
        sb.append(String.format("isBusiness=%s, ",isBusiness()));
        sb.delete(sb.length()-2, sb.length());
        sb.append(String.format(" WHERE id=%d",getId()));

        Connection c = db.getConnection();
        c.createStatement().executeUpdate(sb.toString());
    }

    public void updatePaidRecord(DatabaseConnection db, boolean newPaidValue) throws SQLException {
        SQLitePaidTicket pt = new SQLitePaidTicket(getId());
        if(newPaidValue)
            pt.insertRecordIfNotExists(db);
        else
            pt.deleteRecord(db);
    }

    public void updateBusinessRecord(DatabaseConnection db, boolean newBusinessValue) throws SQLException {
        Connection c = db.getConnection();
        db.atomicTransaction(() -> {
            Ticket temp = TicketData.newBuilderFromPrototype(getRecord(db))
                    .setBusiness(newBusinessValue)
                    .build();

            PreparedStatement st = c.prepareStatement(String.format("""
                UPDATE %s
                SET isBusiness=?, price=?
                WHERE id=?;
                """,
                    TABLE_NAME
            ));
            st.setBoolean(1,newBusinessValue);
            st.setFloat(2,temp.calculatePrice());
            st.setInt(3,getId());
            st.executeUpdate();
        });
    }

    @Override
    public SQLiteTicket getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setInt(1, getId());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return new SQLiteTicket(toRecord(rs));
    }

    @Override
    public Ticket toRecord(ResultSet rs) throws SQLException {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(rs.getLong("trip_departureTime"));

        Route r = new RouteData(
                StationData.newBuilder(rs.getString("departureStation_name"))
                        .setAddress(rs.getString("departure_address"))
                        .setProvince(rs.getString("departure_province"))
                        .setTown(rs.getString("departure_town"))
                        .build(),
                StationData.newBuilder(rs.getString("arrivalStation_name"))
                        .setAddress(rs.getString("arrival_address"))
                        .setProvince(rs.getString("arrival_province"))
                        .setTown(rs.getString("arrival_town"))
                        .build(),
                rs.getInt("route_distance")
        );

        Train t = TrainData.newBuilder(rs.getInt("trip_train_id"))
                .setType(new TrainTypeData(rs.getString("train_type"),rs.getFloat("trainType_price")))
                .setEconomyCapacity(rs.getInt("train_economyCapacity"))
                .setBusinessCapacity(rs.getInt("train_businessCapacity"))
                .build();

        Trip tr = TripData.newBuilder(r)
                .setTrain(t)
                .setDepartureTime(c)
                .setAvailableEconomySeats(rs.getInt("trip_availableEconomySeats"))
                .setAvailableBusinessSeats(rs.getInt("trip_availableBusinessSeats"))
                .build();

        User u = new UserData(
                rs.getString("userEmail"),
                rs.getString("user_password"),
                rs.getBoolean("user_is_fidelity")
        );

        TicketData.Builder b = TicketData.newBuilder(rs.getInt("id"))
                .setUser(u)
                .setName(rs.getString("passenger_name"))
                .setSurname(rs.getString("passenger_surname"))
                .setPrice(rs.getFloat("ticket_price"))
                .setPaid(rs.getBoolean("ticket_is_paid"))
                .setBusiness(rs.getBoolean("is_business"))
                .setTrip(tr);

        String promotion = rs.getString("promotion_code");
        if(promotion != null) {
            b.setPromotion(PromotionData.newBuilder(promotion)
                    .setName(rs.getString("promotion_name"))
                    .setDescription(rs.getString("promotion_description"))
                    .setOnlyFidelityUser(rs.getBoolean("promotion_onlyFidelity"))
                    .setDiscount(rs.getFloat("promotion_discount"))
                    .build());
        }

        return b.build();
    }

    @Override
    public Collection<Ticket> getSimilarRecords(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(SIMILAR_QUERY);
        st.setString(1, getUser().getEmail());
        ResultSet rs = st.executeQuery();

        Collection<Ticket> ret = new LinkedList<>();
        while (rs.next()) ret.add(toRecord(rs));
        return ret;
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setInt(1, getId());
        st.executeUpdate();
    }

    public static boolean hasUserUtilizedPromotion(DatabaseConnection db, User user, Promotion promotion) throws SQLException {
        if (promotion == null) return false;
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(String.format("""
                SELECT * FROM %s WHERE
                userEmail=? AND promotion=?
                """,
                TABLE_NAME
        ));
        st.setString(1, user.getEmail());
        st.setString(2, promotion.getCode());
        ResultSet rs = st.executeQuery();
        return rs.next();
    }

    @Override
    public int getId() {
        return data.getId();
    }

    @Override
    public User getUser() {
        return data.getUser();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public String getSurname() {
        return data.getSurname();
    }

    @Override
    public float getPrice() {
        return data.getPrice();
    }

    @Override
    public Promotion getPromotion() {
        return data.getPromotion();
    }

    @Override
    public Trip getTrip() {
        return data.getTrip();
    }

    @Override
    public boolean isPaid() {
        return data.isPaid();
    }

    @Override
    public boolean isBusiness() {
        return data.isBusiness();
    }
}
