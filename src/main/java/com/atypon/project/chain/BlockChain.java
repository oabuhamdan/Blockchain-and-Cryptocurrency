package com.atypon.project.chain;

import com.atypon.project.transaction.TransactionOutput;

import java.util.*;


// TODO addBlock a method to verify the block chain

/**
 * Represents A blockChain.
 *
 * @author Osama Abuhamdan
 */
public final class BlockChain implements Chain{

    // first block in the chain
    private Block head;

    private List<MyBlock> chain;

    // list of all unspent transactions in the local system.
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();

    /**
     * Create a chain of blocks and generate
     * the first block of it with createGenesisBlock method.
     */
    public BlockChain() {
        chain = new ArrayList<>();
        createGenesisBlock();
    }

    /**
     * Create and addBlock the first block of the chain .
     */
    private void createGenesisBlock() {
        MyBlock genesis = new MyBlock();
        genesis.setParent(null);
        genesis.calculateHash();
        head = genesis;
        chain.add(genesis);
    }

    /**
     * Add newly created block to the chain after setting its number and parent
     *
     * @param block to be added to the chain
     */
    public synchronized void addBlock(MyBlock block) {
        int length = chain.size();
        block.setBlockNumber(length);
        block.setParent(chain.isEmpty() ? null : chain.get(length - 1));
        if (block.getBlockHash() == null) block.calculateHash();
        chain.add(block);
    }

    /**
     * @return blockChain
     */
    public List<MyBlock> getChain() {
        return chain;
    }

    /**
     * Prints all the blocks of the chain
     */
    public void printChain() {
        chain.forEach(System.out::println);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockChain chain1 = (BlockChain) o;
        return Objects.equals(head, chain1.head) &&
                Objects.equals(chain, chain1.chain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, chain);
    }

    @Override
    public String toString() {
        return "BlockChain{" + '\n' +
                ", chain=" + chain + '\n' +
                ", head=" + head + '\n' +
                '}';
    }
}