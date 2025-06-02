package it.trenical.server.auth;

import io.grpc.stub.StreamObserver;
import it.trenical.common.SessionToken;
import it.trenical.grpc.*;
import it.trenical.server.Server;
import it.trenical.server.db.DatabaseConnection;
import it.trenical.server.db.SQLite.SQLiteUser;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.trenical.server.auth.PasswordUtils.verifyPassword;
import static it.trenical.server.auth.PasswordUtils.hashPassword;

public class GrpcAuthImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(GrpcAuthImpl.class);

    private final DatabaseConnection db = Server.INSTANCE.getDatabase();

    private final Server server = Server.INSTANCE;

    private final TokenManager tokenManager = BiMapTokenManager.INSTANCE;

    @Override
    public void login(LoginRequest request, StreamObserver<LoginReply> responseObserver) {

        SQLiteUser user = new SQLiteUser(request.getEmail(), request.getPassword());

        SQLiteUser realUser = null;
        try {
            realUser = user.getRecord(db);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }

        SessionToken token;
        if (realUser != null && verifyPassword(user.getPassword(),realUser.getPassword())) {
            token = tokenManager.getToken(user);
            logger.info("User authenticated successfully: {}", user.getEmail());
        } else {
            token = SessionToken.newInvalidToken();
            logger.info("Failed authentication attempt: {}", user.getEmail());
        }

        LoginReply reply = LoginReply.newBuilder()
                .setToken(token.token())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<LogoutReply> responseObserver) {

        boolean done = tokenManager.remove(new SessionToken(request.getToken()));
        if (done) logger.info("Deauthenticated user: {}", request.getToken());

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
                server.updateUsersCache();
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }

        SessionToken token;
        if (exists) {
            token = SessionToken.newInvalidToken();
        } else {
            token = tokenManager.getToken(user);
            logger.info("User signed up successfully: {}", user.getEmail());
        }

        SignupReply reply = SignupReply.newBuilder().setToken(token.token()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
