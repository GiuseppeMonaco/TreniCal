package it.trenical.server.db.SQLite;

import it.trenical.common.User;
import it.trenical.common.UserData;
import it.trenical.server.db.DatabaseConnection;

import java.sql.*;

public class SQLiteUser implements SQLiteTable<User>, User {
    
    static final String TABLE_NAME = "Users";
    static private final int COLUMNS_NUMBER = 2;

    static private final String COLUMNS =
            "email TEXT(100) NOT NULL, " +
            "password TEXT(100) NOT NULL, " +
            "PRIMARY KEY (email)";

    static private final String INSERT_QUERY =
            SQLiteTable.getInsertQuery(TABLE_NAME, COLUMNS_NUMBER);

    static private final String ALL_QUERY = String.format("""
            SELECT
            u.email,
            u.password,
            -- Fidelity User
            CASE WHEN (
              SELECT uf.userEmail
              FROM %s uf
              WHERE uf.userEmail = u.email
            ) IS NULL THEN 0 ELSE 1 END          AS is_fidelity
            FROM %s u
            """,
            SQLiteFidelityUser.TABLE_NAME,
            TABLE_NAME
            );

    static private final String GET_QUERY = String.format("""
            %s WHERE email=?;
            """,
            ALL_QUERY
            );

    static private final String DELETE_QUERY = String.format("""
            DELETE FROM %s
            WHERE email=?;
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

    private final User data;

    public SQLiteUser(User data) {
        this.data = data;
    }

    public SQLiteUser(String email, String password) {
        this(new UserData(email, password));
    }

    public SQLiteUser(String email, String password, boolean isFidelity) {
        this(new UserData(email, password, isFidelity));
    }

    @Override
    public void insertRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        db.atomicTransaction(() -> {
            PreparedStatement st = c.prepareStatement(INSERT_QUERY);
            st.setString(1, getEmail());
            st.setString(COLUMNS_NUMBER, getPassword());
            st.executeUpdate();
            if(!isFidelity()) return;
            new SQLiteFidelityUser(getEmail()).insertRecord(db);
        });
    }

    @Override
    public void updateRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        db.atomicTransaction(() -> {
            if (getPassword() != null) {
                PreparedStatement st1 = c.prepareStatement(String.format("""
                        UPDATE %s
                        SET password=?
                        WHERE email=?
                        """,
                        TABLE_NAME
                ));
                st1.executeUpdate();
            }
            SQLiteFidelityUser fu = new SQLiteFidelityUser(getEmail());
            if (isFidelity()) {
                fu.insertRecordIfNotExists(db);
            } else {
                fu.deleteRecord(db);
            }
        });
    }

    public boolean checkIfExists(DatabaseConnection db) throws SQLException {
        return getRecord(db) != null;
    }

    @Override
    public SQLiteUser getRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(GET_QUERY);
        st.setString(1, getEmail());
        ResultSet rs = st.executeQuery();

        if (!rs.next()) return null;
        return new SQLiteUser(toRecord(rs));
    }

    @Override
    public User toRecord(ResultSet rs) throws SQLException {
        return new UserData(
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("is_fidelity")
        );
    }

    @Override
    public void deleteRecord(DatabaseConnection db) throws SQLException {
        Connection c = db.getConnection();
        PreparedStatement st = c.prepareStatement(DELETE_QUERY);
        st.setString(1,getEmail());
        st.executeUpdate();
    }

    @Override
    public String getEmail() {
        return data.getEmail();
    }

    @Override
    public String getPassword() {
        return data.getPassword();
    }

    @Override
    public boolean isFidelity() {
        return data.isFidelity();
    }
}