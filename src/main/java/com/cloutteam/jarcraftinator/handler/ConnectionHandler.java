package com.cloutteam.jarcraftinator.handler;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.entity.player.PlayerConnection;
import com.cloutteam.jarcraftinator.logging.LogLevel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler extends Thread {

    private final int port;
    private List<PlayerConnection> connectionList;

    public ConnectionHandler(int port){
        this.port = port;
        connectionList = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (JARCraftinator.isRunning()) {
                Socket socket = serverSocket.accept();
                PlayerConnection connection = new PlayerConnection(socket);
                connection.start();
                connectionList.add(connection);
            }
        } catch (IOException ex) {
            JARCraftinator.getLogger().log("An internal server error occurred.", LogLevel.CRITICAL);
            System.exit(1);
        }
    }

    public List<PlayerConnection> getAllPlayerConnections() {
        return connectionList;
    }

    public void closeConnection(PlayerConnection connection){
        connection.interrupt();
        connectionList.remove(connection);
    }

}
