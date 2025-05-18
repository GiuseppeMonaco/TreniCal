package it.trenical.client.auth;

import java.security.SecureRandom;

public record SessionToken(String token) {

    private static final int TOKEN_LENGHT = 20;

    private static final SecureRandom random = new SecureRandom();
    private static final String CHAR_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789" +
            "~=+%^*/()[]{}!@#$?|";

    public static SessionToken newRandomToken() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGHT);
        for (int i = 0; i < TOKEN_LENGHT; i++) {
            int idx = random.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(idx));
        }
        return new SessionToken(sb.toString());
    }

    public static SessionToken newInvalidToken() {
        return new SessionToken("");
    }

    public boolean isInvalid() {
        return this.token.isEmpty();
    }

}
