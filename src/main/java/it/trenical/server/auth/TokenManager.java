package it.trenical.server.auth;

import it.trenical.client.auth.SessionToken;
import it.trenical.common.User;

public interface TokenManager {

    /**
     * Gets the user associated with a given token if exists.
     * @param token the user's token
     * @return the User if exists, else null
     */
    User getUser(SessionToken token);

    /**
     * Gets the token associated with a given user if exists, else creates a new one.
     * @param user the user
     * @return the Token
     */
    SessionToken getToken(User user);

    /**
     * Remove the give token if exists
     * @param token the token to remove
     * @return true if token existed, else false
     */
    boolean remove(SessionToken token);

}
