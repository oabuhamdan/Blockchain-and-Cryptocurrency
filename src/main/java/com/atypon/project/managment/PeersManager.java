package com.atypon.project.managment;

import com.atypon.project.chain.*;
import com.atypon.project.network.Peer;
import com.atypon.project.util.Utils;

import java.io.*;
import java.util.*;


/**
 * Handle peers functions
 *
 * @author Osama Abuhamdan
 */
public final class PeersManager {

    // noninstantiable
    private PeersManager() {
    }

    // Peers connected to the network. Key is the port number
    private static Map<Integer, Peer> peers = new LinkedHashMap<>();
    private static Scanner in;
    private static Peer peer;
    private static boolean peerAdded = false;

    // file to read serialized data from
    private static final String PEERS_LIST = "peersList.ser";

    /**
     * User can choose a function to do with peers
     */
    public static void chooseAFunction() {

        // update the peers HashMap every 10 seconds
        deserializePeers();

        loop:
        while (true) {
            try {
                in = new Scanner(System.in);
                System.out.println("Choose an option :(4 to printChain options)");

                Scanner in = new Scanner(System.in);

                int option = in.nextInt();
                switch (option) {
                    case 1:
                        if (peerAdded) {
                            System.out.println("To add more peers, run again");
                        } else {
                            addPeer();
                        }
                        break;
                    case 2:
                        removePeer();
                        break;
                    case 3:
                        printPeers();
                        break;
                    case 4:
                        printOptions();
                        break;
                    case 5:
                        peer.getBlockChain().printChain();
                        break;
                    case 6:
                        // back to the main menu
                        break loop;
                    default:
                        System.out.println("Invalid input");
                }
            } catch (Exception e) {
                System.err.println("It seems like you've missed, retry now and be careful");
            }
        }
    }

    /**
     * Add new peer to the network, start it and put it to the HashMap.
     * One peer can added per Run , to addBlock more peers press  run again.
     */
    private static void addPeer() {
        System.out.println("Enter peer name:");
        String portName = in.next();

        // initialize blockChain
        BlockChain chain = new BlockChain();

        // Singleton pattern used here
        peer = new Peer(portName, peers, chain);

        //start the server side of the peer in a new thread
        new Thread(peer::startHost).start();

        //wait 0.5 seconds till the server set-up
        Utils.waitForInMillis(500);

        //addBlock the peer to the peers list
        peers.put(peer.getPort(), peer);

        peerAdded = true;

        serializePeers();
    }

    /**
     * Removes peer from the network by its port number and stop it from accepting requests
     */
    private static void removePeer() {
        printPeers();

        System.out.println("Choose port of peer to remove");
        int port = in.nextInt();

        Peer peer = peers.remove(port);
        System.out.println("Removing peer @ port " + peer.getPort());
        peer.stopHost();
        serializePeers();
    }

    /**
     * Print all peers connected to the network
     */
    private static void printPeers() {
        peers.forEach((key, value) -> System.out.println(value.getName() + ' ' + value));
    }

    /**
     * Add block created by this peer to the chain.
     *
     * @param block block to be added to the chain
     */
    static void addBlockByThisPeer(MyBlock block) {
        peer.addBlockByThisPeer(block);
    }

    /**
     * Show options to do with peers.
     */
    private static void printOptions() {
        System.out.println("1-Add peer" + '\n' +
                "2-Remove peer" + '\n' +
                "3-Print peers" + '\n' +
                "4-Print options" + '\n' +
                "5-Print chain" + '\n' +
                "6-Back to the main menu" + '\n');
    }

    /**
     * Read the peers information from other created threads, it repeats every 10 seconds
     */
    private static void deserializePeers() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(getReadPeersTask(), 0, 10000);
    }

    /**
     * @return reedPeers TimerTask
     */
    private static TimerTask getReadPeersTask() {
        return new TimerTask() {
            public void run() {
                readPeers();
            }
        };
    }

    /**
     * Read the peers information from other created threads
     */
    private static void readPeers() {
        try (FileInputStream fis = new FileInputStream(PEERS_LIST);
             ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis))) {
            while (true) {
                try {
                    Peer peer = (Peer) ois.readObject();
                    peers.put(peer.getPort(), peer);
                } catch (EOFException e) {
                    // break when the end-of-file is reached
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("This is the first peer connected to the network");
        }
    }

    /**
     * Save the peers information
     */
    private static void serializePeers() {
        try (FileOutputStream fos = new FileOutputStream(PEERS_LIST);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            for (Peer peer : peers.values()) {
                oos.writeObject(peer);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}