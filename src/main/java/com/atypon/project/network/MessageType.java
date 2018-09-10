package com.atypon.project.network;

/**
 * Message types that peer can send to other peers
 *
 * @author Osama Abuhamdan
 */
public enum MessageType {

    /**
     * When the peer creates new block and want to broadcast it to other peers
     */
    BROADCAST_BLOCK,

    /**
     * When the peer connected to the network, it asks for the most recent chain available
     */
    REQUEST_CHAIN,
}