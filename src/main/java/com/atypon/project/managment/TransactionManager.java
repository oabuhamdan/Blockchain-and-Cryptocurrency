package com.atypon.project.managment;

import com.atypon.project.chain.MyBlock;
import com.atypon.project.exception.*;
import com.atypon.project.transaction.*;

import java.math.*;
import java.util.*;


/**
 * Handle transactions functions.
 *
 * @author Osama Abuhamdan
 */
public final class TransactionManager {

    // noninstantiable
    private TransactionManager() {
    }

    private static Scanner in;

    // locally created wallets, can be accessed just by the local peer
    private static Map<String, MyWallet> wallets = new HashMap<>();

    /**
     * User can choose a function that makes a transaction.
     */
    public static void chooseAFunction() {
        loop:
        while (true) {
            try {
                in = new Scanner(System.in);
                System.out.println("Choose an option :(6 to printChain options)");

                int option = in.nextInt();
                switch (option) {
                    case 1:
                        createWallet();
                        break;
                    case 2:
                        buyCoins();
                        break;
                    case 3:
                        sendMoney();
                        break;
                    case 4:
                        showBalance();
                        break;
                    case 5:
                        printWallets();
                        break;
                    case 6:
                        printOptions();
                        break;
                    case 7:
                        // back to the main menu
                        break loop;
                    default:
                        System.out.println("Invalid input");
                }
            } catch (NegativeTransactionValueException | NoEnoughFundsException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("It seems like you've missed,retry now and be careful");
            }

        }
    }

    /**
     * Create a wallet and save it to the wallets HashMap
     */
    private static void createWallet() {

        in = new Scanner(System.in);

        System.out.println("Enter Wallet's name :");
        String name = in.nextLine();

        wallets.put(name, new MyWallet());
        System.out.println("Wallet " + name + " was created successfully");
    }

    /**
     * Buy some coins for a certain wallet choose from wallets by its name
     * then put that transaction into a block .
     *
     * @throws NegativeTransactionValueException when user tries to do a negative transaction
     */
    private static void buyCoins() {

        in = new Scanner(System.in);

        System.out.println("Choose a wallet to buy coins for : ");
        String name = in.nextLine();

        System.out.println("Value: ");
        BigDecimal value = in.nextBigDecimal();

        // throws exception if the user tries to send negative valued money
        if (value.signum() != 1) throw new NegativeTransactionValueException();

        MyTransaction trans = wallets.get(name).buyNewCoins(value);
        System.out.println("Wallet " + name + " has bought " + value + " successfully");

        createTransactionAndAddItToBlock(trans);
    }

    /**
     * Send some coins for a certain wallet to another wallet
     * from wallets choose by their name then put that transaction into a block
     *
     * @throws NegativeTransactionValueException when user tries to do a negative transaction
     */
    private static void sendMoney() {

        in = new Scanner(System.in);

        System.out.println("Sender wallet is ?");
        String senderName = in.nextLine();
        MyWallet sender = wallets.get(senderName);

        System.out.println("Receiver wallet is ?");
        String receiverName = in.nextLine();
        MyWallet receiver = wallets.get(receiverName);

        System.out.println("Value to send is is ?");
        BigDecimal value = in.nextBigDecimal();

        // throws exception if the user tries to send negative valued money
        if (value.signum() != 1) throw new NegativeTransactionValueException();

        MyTransaction trans = sender.sendFunds(receiver, value);

        createTransactionAndAddItToBlock(trans);
    }

    /**
     * Add transaction to a block.
     *
     * @param trans transaction to be added to the block
     */
    private static void createTransactionAndAddItToBlock(MyTransaction trans) {
        MyBlock block = new MyBlock();
        block.addTransaction(trans);
        PeersManager.addBlockByThisPeer(block);
    }

    /**
     * Show certain wallet's balance by its name
     */
    private static void showBalance() {
        System.out.println("Choose a wallet :");
        in = new Scanner(System.in);
        String name = in.nextLine();
        System.out.println(wallets.get(name).getBalance());
    }

    /**
     * Print wallets in this system and their balances.
     */
    private static void printWallets() {
        System.out.println("Wallets in this system are : ");
        wallets.forEach((key, value) -> System.out.println(key + " has " + value.getBalance() + '$'));
    }

    /**
     * Options that can be choose in transaction system
     */
    private static void printOptions() {
        System.out.println("1-Create Wallet" + '\n' +
                "2-Buy Coins" + '\n' +
                "3-Transfer Coins" + '\n' +
                "4-Show Balance" + '\n' +
                "5-Print wallets" + '\n' +
                "6-Show options" + '\n' +
                "7-Back to the main menu");
    }
}