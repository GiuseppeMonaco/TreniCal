package it.trenical.server.auth;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.grpc.stub.StreamObserver;
import it.trenical.client.auth.SessionToken;
import it.trenical.common.User;
import it.trenical.grpc.*;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.SQLiteConnection;
import it.trenical.server.db.SQLite.SQLiteUser;

import java.sql.SQLException;
import java.util.logging.Logger;

import static it.trenical.server.auth.PasswordUtils.verifyPassword;
import static it.trenical.server.auth.PasswordUtils.hashPassword;

public class GrpcAuthImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private static final Logger logger = Logger.getLogger(GrpcAuthImpl.class.getName());

    private final DatabaseConnection db = SQLiteConnection.getInstance();

    // Map user's email with current session token
    private final BiMap<String, SessionToken> activeSessions = HashBiMap.create();

    @Override
    public void login(LoginRequest request, StreamObserver<LoginReply> responseObserver) {

        SQLiteUser user = new SQLiteUser(request.getEmail(), request.getPassword());

        SQLiteUser realUser = null;
        try {
            realUser = user.getRecord(db);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        SessionToken token;
        if (realUser != null && verifyPassword(user.getPassword(),realUser.getPassword())) {
            token = getToken(user);
            logger.info(String.format("User authenticated successfully: %s", user.getEmail()));
        } else {
            token = SessionToken.newInvalidToken();
            logger.info(String.format("Failed authentication attempt: %s", user.getEmail()));
        }

        LoginReply reply = LoginReply.newBuilder()
                .setToken(token.token())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<LogoutReply> responseObserver) {

        SessionToken token = new SessionToken(request.getToken());
        boolean done = false;
        if (activeSessions.inverse().containsKey(token)) {
            logger.info(String.format("Deauthenticated user: %s", activeSessions.inverse().get(token)));
            activeSessions.inverse().remove(token);
            done = true;
        }

        LogoutReply reply = LogoutReply.newBuilder().setIsDone(done).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void signup(SignupRequest request, StreamObserver<SignupReply> responseObserver) {

        SQLiteUser user = new SQLiteUser(request.getEmail(), request.getPassword());

        boolean exists = true;
        try {
            exists = user.checkIfExists(db);
            if (!exists) {
                new SQLiteUser(user.getEmail(), hashPassword(user.getPassword())).insertRecord(db);
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        SessionToken token;
        if (exists) {
            token = SessionToken.newInvalidToken();
        } else {
            token = getToken(user);
            logger.info(String.format("User signed up successfully: %s", user.getEmail()));
        }

        SignupReply reply = SignupReply.newBuilder().setToken(token.token()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    private SessionToken getToken(User user) {
        SessionToken token;
        if (activeSessions.containsKey(user.getEmail())) {
            token = activeSessions.get(user.getEmail());
            logger.info(String.format("User has already a token: %s", user.getEmail()));
        } else {
            do {
                token = SessionToken.newRandomToken();
            } while (activeSessions.inverse().containsKey(token));
            activeSessions.put(user.getEmail(), token);
        }
        return token;
    }

}
