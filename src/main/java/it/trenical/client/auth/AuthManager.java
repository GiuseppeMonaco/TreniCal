package it.trenical.client.auth;

import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.common.User;

public interface AuthManager {

    SessionToken login(User user) throws InvalidCredentialsException;
    void logout(SessionToken token) throws InvalidSessionTokenException;
    SessionToken signup(User user) throws InvalidCredentialsException, UserAlreadyExistsException;

}
