package it.trenical.client;

import it.trenical.client.auth.AuthManager;
import it.trenical.client.auth.GrpcAuthManager;
import it.trenical.client.auth.SessionToken;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.User;
import it.trenical.common.UserData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    // Singleton class
    private static Client instance;

    Logger logger = LoggerFactory.getLogger(Client.class);

    private final AuthManager auth;

    private User currentUser;
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
        currentUser = new UserData(user.getEmail());
        logger.info("Login effettuato come {}", user.getEmail());
    }

    public void logout() throws UnreachableServer {
        try {
            auth.logout(token);
            logger.info("Logout effettuato");
        } catch (InvalidSessionTokenException e) {
            logger.warn("Token was invalid: {}", token.token());
        }
        token = null;
        currentUser = null;
    }

    public void signup(User user) throws InvalidCredentialsException, UserAlreadyExistsException, UnreachableServer {
        token = auth.signup(user);
        currentUser = new UserData(user.getEmail());
        logger.info("Signup effettuato come {}", user.getEmail());
    }

    public boolean isAuthenticated() {
        return token != null && currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

}
