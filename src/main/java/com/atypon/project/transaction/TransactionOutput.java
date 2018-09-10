package com.atypon.project.transaction;

import com.atypon.project.util.Utils;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.security.PublicKey;

/**
 * Represents the output of the transaction
 *
 * @author Osama Abuhamdan
 */
final public class TransactionOutput {

    private final PublicKey recipient;
    private final String id;
    private final BigDecimal value;

    TransactionOutput(PublicKey recipient, BigDecimal value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;

        // the id of the transaction this output was created in
        this.id = DigestUtils.sha256Hex(Utils.getStringFromKey(recipient) + value + parentTransactionId);
    }

    String getId() {
        return id;
    }

    BigDecimal getValue() {
        return value;
    }

    /**
     * check if coin belongs to you
     */
    boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}