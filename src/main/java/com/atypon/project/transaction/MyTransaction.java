package com.atypon.project.transaction;

import com.atypon.project.chain.BlockChain;
import com.atypon.project.util.Utils;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.security.*;
import java.util.*;

/**
 * Represents the transactions in the system.
 *
 * @author Osama Abuhamdan
 */
public final class MyTransaction implements Transaction {

    // hash of the transaction
    private final String transactionId;

    // senders address/public key
    private final PublicKey sender;

    // recipients address/public key
    private final PublicKey recipient;

    // to be sent
    private final BigDecimal value;

    // to prevent unauthorized people from spending funds in the wallet
    private byte[] signature;

    // which are references to previous transactions that prove the sender has funds to send
    private final List<TransactionInput> inputs;

    // outputs, which shows the amount relevant addresses received in the transaction.
    // (these outputs are referenced as inputs in new transactions)
    private final List<TransactionOutput> outputs = new LinkedList<>();

    private static int sequence = 0; //count of how many transactions have been generated.

    /**
     * Create new transaction
     *
     * @param from   Sender MyWallet
     * @param to     Receiver MyWallet
     * @param value  Money to transfer
     * @param inputs The sources of the money sender going to spent.
     */
    MyTransaction(MyWallet from, MyWallet to, BigDecimal value, List<TransactionInput> inputs) {
        this.sender = from.getPublicKey();
        this.recipient = to.getPublicKey();
        this.inputs = inputs;
        this.value = value;

        transactionId = calculateHash();
    }

    /**
     * @return transaction hash
     */
    String getTransactionId() {
        return transactionId;
    }

    /**
     * @return arrayList of generated outputs by this transaction
     */
    List<TransactionOutput> getOutputs() {
        return outputs;
    }

    /**
     * Calculate the transaction hash (which will be used as its Id)
     *
     * @return hash of the transaction
     */
    public String calculateHash() {
        String concatenated = Utils.getStringFromKey(sender) +
                Utils.getStringFromKey(recipient) +
                value.toString() +
                ++sequence;
        return DigestUtils.sha256Hex(concatenated);
    }

    /**
     * Generates a secret signature for the transaction , to prevent unauthorized people
     * from tampering with it
     *
     * @param privateKey to be used for generating the signature
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = Utils.getStringFromKey(sender) + Utils.getStringFromKey(recipient) + value.toString();
        signature = Utils.applyECDSASig(privateKey, data);
    }

    /**
     * Make sure that the authorized person is going to tamper this transaction.
     *
     * @return true if the signature verified
     */
    public boolean verifySignature() {
        return Utils.verifyECDSASig(sender, signature,
                Utils.getStringFromKey(sender) + Utils.getStringFromKey(recipient) + value.toString());
    }

    /**
     * Add the money to the receiver account and remove them from sender account
     */
    void processTransaction() {

        //get value of inputs then the left over change:
        BigDecimal leftOver = getInputsValue().subtract(value);

        // send value to recipient
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));

        // send the left over 'change' back to sender
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        // addBlock outputs to Unspent list
        for (TransactionOutput output : outputs) {
            BlockChain.UTXOs.put(output.getId(), output);
        }

        // remove transaction inputs from UTXO lists as spent
        for (TransactionInput input : inputs) {
            //if MyTransaction can't be found skip it
            if (input.UTXO == null) continue;
            BlockChain.UTXOs.remove(input.UTXO.getId());
        }
    }

    /**
     * @return Sum of inputs value to this transaction, to be spent in other transactions.
     */
    private BigDecimal getInputsValue() {
        BigDecimal total = new BigDecimal("0");
        for (TransactionInput i : inputs) {
            // if MyTransaction can't be found skip it
            if (i.UTXO == null) continue;
            total = (total).add(i.UTXO.getValue());
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyTransaction that = (MyTransaction) o;
        return Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(recipient, that.recipient) &&
                Objects.equals(value, that.value) &&
                Arrays.equals(signature, that.signature) &&
                Objects.equals(inputs, that.inputs) &&
                Objects.equals(outputs, that.outputs);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(transactionId, sender, recipient, value, inputs, outputs);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    @Override
    public String toString() {
        return "MyTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", value=" + value +
                ", signature=" + Arrays.toString(signature) +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }
}