package it.trenical.client.request;

import it.trenical.client.request.exceptions.*;
import it.trenical.common.SessionToken;
import it.trenical.client.auth.exceptions.InvalidSessionTokenException;
import it.trenical.client.connection.exceptions.UnreachableServer;
import it.trenical.common.Ticket;

import java.util.Collection;

public interface RequestManager {

    /**
     * Buy the given tickets for the user associated to the given token.
     * @param token the token of the user
     * @param tickets a list containing the tickets to buy
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     * @throws InvalidSeatsNumberException if there are no seats available
     * @throws CancelledTripException if the trip of a ticket is no longer available
     * @throws CancelledPromotionException if the promotion of a ticket is no longer available
     */
    void buyTickets(SessionToken token, Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidSeatsNumberException, CancelledTripException, CancelledPromotionException;

    /**
     * Book the given tickets for the user associated to the given token.
     * @param token the token of the user
     * @param tickets a list containing the tickets to book
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     * @throws InvalidSeatsNumberException if there are no seats available
     * @throws CancelledTripException if the trip of a ticket is no longer available
     * @throws CancelledPromotionException if the promotion of a ticket is no longer available
     */
    void bookTickets(SessionToken token, Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidSeatsNumberException, CancelledTripException, CancelledPromotionException;

    /**
     * Pay the given booked tickets of the user associated to the given token.
     * @param token the token of the user
     * @param tickets a list containing the booked tickets to buy
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     * @throws InvalidTicketException if at least one ticket is not a booked ticket by the user associated with the token
     */
    void payBookedTickets(SessionToken token, Collection<Ticket> tickets) throws UnreachableServer, InvalidSessionTokenException, InvalidTicketException;

    /**
     * Edit the given ticket (buyed or booked) of the user associated to the given token.
     * @param token the token of the user
     * @param ticket the ticket to edit
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     * @throws InvalidTicketException if at least one ticket is not a ticket of the user associated with the token
     * @throws NoChangeException if the ticket is already in the state passed as parameter
     * @throws InvalidSeatsNumberException if there are no seats available
     */
    void editTicket(SessionToken token, Ticket ticket) throws UnreachableServer, InvalidSessionTokenException, InvalidTicketException, NoChangeException, InvalidSeatsNumberException;

    /**
     * Subscribe the user associated to the given token to Fidelity program.
     * @param token the token of the user
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     * @throws NoChangeException if the user is already a Fidelity user
     */
    void becomeFidelity(SessionToken token) throws UnreachableServer, InvalidSessionTokenException, NoChangeException;

    /**
     * Unsubscribe the user associated to the given token from Fidelity program.
     * @param token the token of the user
     * @throws UnreachableServer if server is unreachable
     * @throws InvalidSessionTokenException if token not exists
     * @throws NoChangeException if the user isn't already a Fidelity user
     */
    void cancelFidelity(SessionToken token) throws UnreachableServer, InvalidSessionTokenException, NoChangeException;
}
