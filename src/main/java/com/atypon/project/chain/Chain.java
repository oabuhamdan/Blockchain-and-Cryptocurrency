package com.atypon.project.chain;

/**
 * Represents the basic actions a block chain must implement.
 */
public interface Chain {

    void addBlock(MyBlock block);

    void printChain();

}
