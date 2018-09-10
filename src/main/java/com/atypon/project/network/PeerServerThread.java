package com.atypon.project.network;

import com.atypon.project.chain.MyBlock;

import java.io.*;
import java.net.Socket;

/**
 * Represents Server side of the peer.
 *
 * @author Osama Abuhamdan
 */
public final class PeerServerThread implements Runnable {

    private Socket client;
    private Peer peer;

    /**
     * @param client connected to the peer
     * @param peer   Server's peer
     */
    PeerServerThread(Socket client, Peer peer) {
        this.client = client;
        this.peer = peer;
    }

    /**
     * Create new thread to handle client request.
     */
    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

            Object receivedFromClient = in.readObject();

            // if the client asks for the chain , send it it.
            if (receivedFromClient instanceof String) {
                String msg = (String) receivedFromClient;
                if (msg.equals("Send Chain")) {
                    out.writeObject(peer.getBlockChain().getChain());
                }
            }

            // if the client send me a new block , addBlock it to the chain
            if (receivedFromClient instanceof MyBlock) {
                MyBlock receivedBlock = (MyBlock) receivedFromClient;
                System.out.println("Peer " + peer.getName() + " has received a new MyBlock");

                peer.addBlockByAnotherPeer(receivedBlock);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}