package com.atypon.project.transaction;


/**
 * Represents the inputs of a certain transaction.
 *
 * @author Osama Abuhamdan
 */
final class TransactionInput {

    // reference to TransactionOutputs -> transactionId
    private final String transactionOutputId;

    // contains the Unspent transaction output
    TransactionOutput UTXO;

    String getTransactionOutputId() {
        return transactionOutputId;
    }

    TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}