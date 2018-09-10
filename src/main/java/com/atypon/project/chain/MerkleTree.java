package com.atypon.project.chain;


import com.atypon.project.transaction.MyTransaction;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

/**
 * represents the merkle tree data structure used in transaction system in more
 * professional cryptocurrency systems
 */
final class MerkleTree {

    // Noninstantiable
    private MerkleTree() {
    }

    // root ode of the tree that represents the hash of all
    // the transactions in it combined
    private static String root;

    /**
     * build the merkle tree
     *
     * @param transactionList transactions to make the tree
     */
    static void buildTree(List<MyTransaction> transactionList) {
        if (transactionList.size() == 0) {
            root = "";
            return;
        }

        List<String> stringTransList = new ArrayList<>();

        for (MyTransaction aTransactionList : transactionList) {
            stringTransList.add(aTransactionList.calculateHash());
        }

        List<String> hashedTransList = getHashedList(stringTransList);

        while (hashedTransList.size() != 1) {
            hashedTransList = getHashedList(hashedTransList);
        }

        root = hashedTransList.get(0);
    }

    /**
     * @param stringTransList tree before getting hashed
     * @return tree after getting hashed
     */
    private static List<String> getHashedList(List<String> stringTransList) {
        List<String> tempHashedTransList = new ArrayList<>();

        int index = 0;
        while (index < stringTransList.size()) {
            // left
            String left = stringTransList.get(index);
            index++;

            // right
            String right = "";
            if (index != stringTransList.size()) {
                right = stringTransList.get(index);
            }

            // sha2 hex value
            String hashedValue = getHashedValue(left + right);
            tempHashedTransList.add(hashedValue);
            index++;
        }

        return tempHashedTransList;
    }

    /**
     * @param str element of the tree to be hashed
     * @return element after it got hashed
     */
    private static String getHashedValue(String str) {
        return DigestUtils.sha256Hex(str);
    }

    /**
     * @return root of the tree
     */
    static String getRoot() {
        return root;
    }
}