package com.atypon.project.transaction;

import java.security.PrivateKey;

/**
 * Represents the basic actions a transaction within the block chain must implement.
 */
public interface Transaction {

    String calculateHash();

    void generateSignature(PrivateKey privateKey);

    boolean verifySignature();

}
