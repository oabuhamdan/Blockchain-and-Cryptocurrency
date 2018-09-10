package com.atypon.project.exception;

/**
 * Thrown when the user try to send money he don't have
 *
 * @author Osama Abuhamdan
 * @see java.lang.RuntimeException
 */
public final class NoEnoughFundsException extends RuntimeException {
    public NoEnoughFundsException() {
        super("No enough funds to complete the transaction");
    }
}
