package it.trenical.client;

import it.trenical.client.auth.AuthManager;
import it.trenical.client.auth.GrpcAuthManager;
import it.trenical.client.auth.SessionToken;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.User;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    // Singleton class
    private static Client instance;

    Logger logger = Logger.getLogger(Client.class.getName());

    private final AuthManager auth;

    private SessionToken token;

    private Client() {
        auth = new GrpcAuthManager();
    }

    public static synchronized Client getInstance() {
        if (instance == null) instance = new Client();
        return instance;
    }

    public void login(User user) throws InvalidCredentialsException, UnreachableServer {
        token = auth.login(user);
        logger.log(Level.INFO, "Login effettuato come {0}", user.getEmail());
    }

    public void logout() throws UnreachableServer {
        try {
            auth.logout(token);
            logger.info("Logout effettuato");
        } catch (InvalidSessionTokenException e) {
            logger.log(Level.WARNING, "Token was invalid: {0}", token.token());
        }
        token = null;
    }

    public void signup(User user) throws InvalidCredentialsException, UserAlreadyExistsException, UnreachableServer {
        token = auth.signup(user);
        logger.log(Level.INFO, "Signup effettuato come {0}", user.getEmail());
    }

    public boolean isAuthenticated() {
        return token != null;
    }

}
