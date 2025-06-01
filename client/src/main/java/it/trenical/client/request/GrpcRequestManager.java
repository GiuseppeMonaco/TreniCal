package it.trenical.client.request;

import io.grpc.StatusRuntimeException;
import it.trenical.client.request.exceptions.InvalidSeatsNumberException;
import it.trenical.common.SessionToken;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.GrpcConnection;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.client.request.exceptions.InvalidTicketException;
import it.trenical.client.request.exceptions.NoChangeException;
import it.trenical.grpcUtil.GrpcConverter;
import it.trenical.common.Ticket;
import it.trenical.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class GrpcRequestManager implements RequestManager {

    private static final Logger logger = LoggerFactory.getLogger(GrpcRequestManager.class);

    private final RequestServiceGrpc.RequestServiceBlockingStub blockingStub;

    public GrpcRequestManager() {
        blockingStub = RequestServiceGrpc.newBlockingStub(GrpcConnection.getChannel());
    }

    private static final int SUCCESS_CODE = 0;
    private static final int INVALID_TOKEN_ERROR_CODE = 1;
    private static final int GENERIC_ERROR_CODE = 2;
    private static final int INVALID_TICKET_ERROR_CODE = 3;
    private static final int NO_CHANGE_ERROR_CODE = 4;
    private static final int NOT_AVAILABLE_SEATS_ERROR_CODE = 5;

    @Override
    public void buyTickets(SessionToken token, Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidSeatsNumberException {
        BuyTicketsRequest request = BuyTicketsRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .addAllTickets(tickets.stream().map(GrpcConverter::convert).toList())
                .build();

        try {
            switch (blockingStub.buyTickets(request).getErrorCode()) {
                case SUCCESS_CODE -> {}
                case INVALID_TOKEN_ERROR_CODE -> throw new InvalidSessionTokenException("Given token is invalid");
                case NOT_AVAILABLE_SEATS_ERROR_CODE -> throw new InvalidSeatsNumberException("There are not enough seats to buy");
                default -> logger.warn("Unknown error on buyTickets request");
            }
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying buyTickets");
            throw new UnreachableServer("Unreachable server");
        }
    }

    @Override
    public void bookTickets(SessionToken token, Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidSeatsNumberException {
        BookTicketsRequest request = BookTicketsRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .addAllTickets(tickets.stream().map(GrpcConverter::convert).toList())
                .build();

        try {
            switch (blockingStub.bookTickets(request).getErrorCode()) {
                case SUCCESS_CODE -> {}
                case INVALID_TOKEN_ERROR_CODE -> throw new InvalidSessionTokenException("Given token is invalid");
                case NOT_AVAILABLE_SEATS_ERROR_CODE -> throw new InvalidSeatsNumberException("There are not enough seats to book");
                default -> logger.warn("Unknown error on bookTickets request");
            }
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying bookTickets");
            throw new UnreachableServer("Unreachable server");
        }
    }

    @Override
    public void payBookedTickets(SessionToken token, Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidTicketException {
        PayBookedTicketsRequest request = PayBookedTicketsRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .addAllTickets(tickets.stream().map(GrpcConverter::convert).toList())
                .build();

        try {
            switch (blockingStub.payBookedTickets(request).getErrorCode()) {
                case SUCCESS_CODE -> {}
                case INVALID_TOKEN_ERROR_CODE -> throw new InvalidSessionTokenException("Given token is invalid");
                case INVALID_TICKET_ERROR_CODE -> throw new InvalidTicketException("Given tickets are invalid");
                default -> logger.warn("Unknown error on payBookedTickets request");
            }
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying payBookedTickets");
            throw new UnreachableServer("Unreachable server");
        }
    }

    @Override
    public void editTicket(SessionToken token, Ticket ticket) throws UnreachableServer, InvalidSessionTokenException, InvalidTicketException, NoChangeException, InvalidSeatsNumberException {
        EditTicketRequest request = EditTicketRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .setTicket(GrpcConverter.convert(ticket))
                .build();

        try {
            switch (blockingStub.editTicket(request).getErrorCode()) {
                case SUCCESS_CODE -> {}
                case INVALID_TOKEN_ERROR_CODE -> throw new InvalidSessionTokenException("Given token is invalid");
                case INVALID_TICKET_ERROR_CODE -> throw new InvalidTicketException("Given ticket is invalid");
                case NO_CHANGE_ERROR_CODE -> throw new NoChangeException("Given ticket has no change");
                case NOT_AVAILABLE_SEATS_ERROR_CODE -> throw new InvalidSeatsNumberException("There are not enough seats for the switch");
                default -> logger.warn("Unknown error on editTicket request");
            }
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying editTicket");
            throw new UnreachableServer("Unreachable server");
        }
    }

    @Override
    public void becomeFidelity(SessionToken token) throws UnreachableServer, InvalidSessionTokenException, NoChangeException {
        BecomeFidelityRequest request = BecomeFidelityRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .build();

        try {
            switch (blockingStub.becomeFidelity(request).getErrorCode()) {
                case SUCCESS_CODE -> {}
                case INVALID_TOKEN_ERROR_CODE -> throw new InvalidSessionTokenException("Given token is invalid");
                case NO_CHANGE_ERROR_CODE -> throw new NoChangeException("User was already subscribed to Fidelity program");
                default -> logger.warn("Unknown error on becomeFidelity request");
            }
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying becomeFidelity");
            throw new UnreachableServer("Unreachable server");
        }
    }

    @Override
    public void cancelFidelity(SessionToken token) throws UnreachableServer, InvalidSessionTokenException, NoChangeException {
        CancelFidelityRequest request = CancelFidelityRequest.newBuilder()
                .setToken(GrpcConverter.convert(token))
                .build();

        try {
            switch (blockingStub.cancelFidelity(request).getErrorCode()) {
                case SUCCESS_CODE -> {}
                case INVALID_TOKEN_ERROR_CODE -> throw new InvalidSessionTokenException("Given token is invalid");
                case NO_CHANGE_ERROR_CODE -> throw new NoChangeException("User was already not subscribed to Fidelity program");
                default -> logger.warn("Unknown error on cancelFidelity request");
            }
        } catch (StatusRuntimeException e) {
            logger.warn("Unreachable server trying cancelFidelity");
            throw new UnreachableServer("Unreachable server");
        }
    }
}
