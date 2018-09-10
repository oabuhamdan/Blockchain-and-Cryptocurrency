package com.atypon.project.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

/**
 * Represents the basic actions wallet must implement.
 */
public interface Wallet {

    MyTransaction sendFunds(MyWallet recipient, BigDecimal value);

    PublicKey getPublicKey();

    BigDecimal getBalance();

}
