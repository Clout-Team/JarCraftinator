package com.cloutteam.jarcraftinator.handler;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.player.PlayerConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(25565);
            while (true) {
                Socket socket = serverSocket.accept();
                new PlayerConnection(socket).start();
            }
        } catch (IOException ex) {
            JARCraftinator.err("Failed to bind to port!", "The port your defined is already being used by another program!");
        }
    }
}
