package it.trenical.common;

public class UserData implements User {

    private final String email;
    private final String password;
    private final boolean isFidelity;

    public UserData(String email, String password, boolean isFidelity) {
        this.email = email;
        this.password = password;
        this.isFidelity = isFidelity;
    }

    public UserData(String email, String password) {
        this(email, password, false);
    }

    public UserData(String email) {
        this(email, null);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isFidelity() {
        return isFidelity;
    }
}
