package com.atypon.project.network;

import java.io.*;
import java.net.ServerSocket;

/**
 * Server Socket class that can be serialized
 *
 * @author Osama Abuhamdan
 */
final class SerializableServerSocket extends ServerSocket implements Serializable {

    SerializableServerSocket(int port) throws IOException {
        super(port);
    }
}