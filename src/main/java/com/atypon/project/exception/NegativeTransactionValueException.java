package com.atypon.project.exception;

/**
 * Thrown when the user try to buy or send money with negative value
 *
 * @author Osama Abuhamdan
 * @see java.lang.RuntimeException
 */
public final class NegativeTransactionValueException extends RuntimeException {
    public NegativeTransactionValueException() {
        super("Negative value transaction can't be done");
    }
}
