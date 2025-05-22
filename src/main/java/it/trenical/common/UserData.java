package it.trenical.common;

public class UserData implements User {

    private final String email;
    private final String password;
    private final boolean isFidelity;

    public UserData(String email, String password, boolean isFidelity) {
        if (email == null) throw new IllegalArgumentException("email cannot be null");
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

    @Override
    public final boolean equals(Object o) {
        if(o == this) return true;
        if (!(o instanceof UserData userData)) return false;
        return email.equals(userData.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
