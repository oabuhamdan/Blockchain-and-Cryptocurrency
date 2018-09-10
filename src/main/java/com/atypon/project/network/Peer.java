package com.atypon.project.network;

import com.atypon.project.chain.MyBlock;
import com.atypon.project.chain.BlockChain;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static com.atypon.project.network.MessageType.*;

/**
 * Represents Peer
 *
 * @author Osama Abuhamdan
 */
public final class Peer implements Serializable {

    private static final long serialVersionUID = 199774_199774_199774L;

    private static final String LOCAL_HOST = "localhost";

    // Chain
    // local block chain
    private transient final BlockChain chain;

    /**
     * @serialFields
     */
    // Peer Info
    private final String name;
    private int port;

    // peers connected to the network
    private Map<Integer, Peer> peers;


    // Server Info
    private SerializableServerSocket server;

    // listening to other peers requests
    private boolean listening = true;

    /**
     * Create new peer
     *
     * @param name  peer name
     * @param peers connected to the network
     */
    public Peer(String name, Map<Integer, Peer> peers, BlockChain blockChain) {
        this.peers = peers;
        this.name = name;
        this.chain = blockChain;
    }

    /**
     * @return local block chain.
     */
    public BlockChain getBlockChain() {
        return chain;
    }

    /**
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * @return peer's name
     */
    public String getName() {
        return name;
    }

    /**
     * Peer start accepting requests from other peers, makes a new thread for every
     * peer want to connect to it.
     */
    public void startHost() {
        //  bind 0 to port, system-generated port will be used
        try (SerializableServerSocket serverSocket = server = new SerializableServerSocket(0)) {

            // set the system-generated port to peer
            setPort(serverSocket.getLocalPort());

            System.out.println("Peer " + name + " connected @ port : " + serverSocket.getLocalPort());
            broadcast(REQUEST_CHAIN, null);
            listening = true;
            while (listening) {
                acceptRequest(serverSocket);
            }
        } catch (IOException e) {
            System.err.println("Could not listen to port: " + port);
        }
    }

    /**
     * Accept requests from other peers
     *
     * @param serverSocket going to accept requests
     * @throws IOException if server was'nt initialized correctly
     */
    private void acceptRequest(SerializableServerSocket serverSocket) throws IOException {
        Socket client = serverSocket.accept();
        Thread thread = new Thread(new PeerServerThread(client, this));
        thread.start();
    }

    /**
     * Set the system-generated port to peer
     *
     * @param port chosen by the system
     */
    private void setPort(int port) {
        this.port = port;
    }

    /**
     * Peer stops accepting requests from other peers.
     */
    public void stopHost() {
        listening = false;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Blocks created by other peers will be added to the chain
     *
     * @param block to be added to the chain , broadcast by other peers.
     */
    void addBlockByAnotherPeer(MyBlock block) {
        System.out.println("Block has been added to the chain");
        chain.addBlock(block);
    }

    /**
     * Blocks created by this peer will be added to the chain
     *
     * @param block to be added to chain by this peer , then been broadcast to other peers.
     */
    public void addBlockByThisPeer(MyBlock block) {
        chain.addBlock(block);
        System.out.println("new Block added by peer " + name + " and being broadcast");
        broadcast(BROADCAST_BLOCK, block);
    }

    // if the chain received to this peer stop asking it from other peers.
    private transient boolean chainReceived = false;

    /**
     * Broadcast some message to all the peers connected to this network
     *
     * @param messageType to be send to other peers.
     * @param block       to be broadcast to other peers if the message type is BROADCAST_BLOCK.
     */
    private void broadcast(MessageType messageType, MyBlock block) {
        for (Peer peer : peers.values()) {

            // don't send my message back to me
            if (peer.equals(this)) {
                continue;
            }

            // whenever the peer asked for the chain has received it , stop asking it from other peers
            if (chainReceived && messageType == REQUEST_CHAIN) {
                break;
            }

            handleMessages(peer, messageType, block);
        }
    }

    /**
     * Send the message peer want to broadcast
     *
     * @param peer        to send message to it
     * @param messageType type of the message
     * @param block       block to be broadcast if the message type is BROADCAST_BLOCK
     */
    private void handleMessages(Peer peer, MessageType messageType, MyBlock block) {
        try {
            Socket socket = new Socket(LOCAL_HOST, peer.port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            // Add blocks from other peers to the current block chain, called on the first time peer
            // connected to the network
            if (messageType == REQUEST_CHAIN) {
                System.out.println("Peer " + this.name + " is asking for chain from Peer " + peer.name);
                out.writeObject("Send Chain");
                handleReceivingChain(peer.name, socket);
            }

            // broadcast newly created block to other peers
            if (messageType == BROADCAST_BLOCK) {
                System.out.println("Peer " + this.name + " is broadcasting a block to Peer " + peer.name);

                // send block object to the beer
                out.writeObject(block);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add blocks from other peers to the current block chain , called on the first time peer
     * connected to the network
     *
     * @param name   peer name
     * @param socket to be connected to
     */
    private void handleReceivingChain(String name, Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            Object receivedFromPeer = in.readObject();
            System.out.println("Peer " + this.name + " has received a chain from Peer " + name);

            //receiveFromPeer is an arrayList, so cast is correct.
            @SuppressWarnings("unchecked")
            List<MyBlock> receivedChain = (ArrayList<MyBlock>) receivedFromPeer;

            if (!receivedChain.isEmpty()) {
                // replace the original chain with the new chain.
                chain.getChain().clear();
                receivedChain.forEach(chain::addBlock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        chainReceived = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return port == peer.port;

    }

    @Override
    public int hashCode() {
        return Objects.hash(port, LOCAL_HOST, peers, server, listening);
    }

    @Override
    public String toString() {
        return "port=" + port +
                ", host=" + LOCAL_HOST;
    }
}