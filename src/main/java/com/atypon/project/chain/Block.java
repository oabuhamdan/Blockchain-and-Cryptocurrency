package com.atypon.project.chain;

import com.atypon.project.transaction.MyTransaction;

/**
 * Represents the basic actions a block in a block chain must implement.
 */
public interface Block {
    void calculateHash();

    void addTransaction(MyTransaction transaction);
}
