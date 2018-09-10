package com.atypon.project;

import com.atypon.project.managment.*;
import com.atypon.project.util.Utils;

import java.util.Scanner;

//TODO make a GUI version of this program.

/**
 * Start the program.
 *
 * @author Osama Abuhamdan
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("******************************************************" + '\n' +
                "* Welcome to block-chain and crypto-currency program * " + '\n' +
                "******************************************************");

        Utils.waitForInMillis(1000);

        start();
    }


    private static void start() {
        while (true) {
            try {
                Scanner in = new Scanner(System.in);
                System.out.println("Enter:" +
                        "\n1-for peers management" +
                        "\n2-for Transaction management");

                switch (in.nextInt()) {
                    case 1:
                        PeersManager.chooseAFunction();
                        break;
                    case 2:
                        TransactionManager.chooseAFunction();
                        break;
                }
            } catch (Exception e) {
                System.err.println("It seems like you've missed,retry now and be careful");
            }
        }
    }
}




