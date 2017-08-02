package com.cloutteam.jarcraftinator.handler;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.player.PlayerConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private int port;

    public ConnectionHandler(int port){
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new PlayerConnection(socket).start();
            }
        } catch (IOException ex) {
            JARCraftinator.err("Port " + port + " is already in use!\n\nCheck that:\n1. There isn't another application running on port " + port + ".\n2. There aren't other instances of the server still running.");
        }
    }
}
