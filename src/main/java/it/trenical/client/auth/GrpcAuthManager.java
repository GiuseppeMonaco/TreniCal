package it.trenical.client.auth;

import io.grpc.StatusRuntimeException;
import it.trenical.client.auth.exceptions.InvalidCredentialsException;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.auth.exceptions.UserAlreadyExistsException;
import it.trenical.client.connection.GrpcConnection;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.User;
import it.trenical.grpc.*;

import java.util.logging.Logger;

public class GrpcAuthManager implements AuthManager {

    private static final Logger logger = Logger.getLogger(GrpcAuthManager.class.getName());

    private final AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    public GrpcAuthManager() {
        blockingStub = AuthServiceGrpc.newBlockingStub(GrpcConnection.getChannel());
    }

    @Override
    public SessionToken login(User user) throws InvalidCredentialsException, UnreachableServer {

        LoginRequest request = LoginRequest.newBuilder()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .build();

        try {
            SessionToken reply = new SessionToken(blockingStub.login(request).getToken());
            if (reply.isInvalid())
                throw new InvalidCredentialsException(String.format("Credentials are not valid for %s",user.getEmail()));
            return reply;
        } catch (StatusRuntimeException e) {
            logger.warning("Server irraggiungibile");
            throw new UnreachableServer("Server irraggiungibile");
        }
    }

    @Override
    public void logout(SessionToken token) throws InvalidSessionTokenException, UnreachableServer {

        LogoutRequest request = LogoutRequest.newBuilder()
                .setToken(token.token())
                .build();

        try {
            if (!blockingStub.logout(request).getIsDone())
                throw new InvalidSessionTokenException(String.format("%s is not a valid token", token.token()));
        } catch (StatusRuntimeException e) {
            logger.warning("Server irraggiungibile");
            throw new UnreachableServer("Server irraggiungibile");
        }
    }

    @Override
    public SessionToken signup(User user) throws InvalidCredentialsException, UserAlreadyExistsException, UnreachableServer { // TODO gestire l'eccezione

        SignupRequest request = SignupRequest.newBuilder()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .build();

        try {
            SessionToken reply = new SessionToken(blockingStub.signup(request).getToken());
            if (reply.isInvalid())
                throw new UserAlreadyExistsException(String.format("User %s already exists",user.getEmail()));
            return reply;
        } catch (StatusRuntimeException e) {
            logger.warning("Server irraggiungibile");
            throw new UnreachableServer("Server irraggiungibile");
        }
    }
}
