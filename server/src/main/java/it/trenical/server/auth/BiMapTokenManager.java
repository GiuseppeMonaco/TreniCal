package it.trenical.server.auth;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.trenical.common.SessionToken;
import it.trenical.common.User;
import it.trenical.common.UserData;

// Singleton class
public enum BiMapTokenManager implements TokenManager {
    INSTANCE;

    // Map user's email with current session token
    private final BiMap<User, SessionToken> activeSessions = HashBiMap.create();

    @Override
    public synchronized User getUser(SessionToken token) {
        return activeSessions.inverse().getOrDefault(token, null);
    }

    @Override
    public synchronized SessionToken getToken(User user) {
        user = new UserData(user.getEmail());
        SessionToken token;
        if (activeSessions.containsKey(user)) {
            token = activeSessions.get(user);
        } else {
            do {
                token = SessionToken.newRandomToken();
            } while (activeSessions.inverse().containsKey(token));
            activeSessions.put(user, token);
        }
        return token;
    }

    @Override
    public synchronized boolean remove(SessionToken token) {
        return activeSessions.inverse().remove(token) != null;
    }

}
