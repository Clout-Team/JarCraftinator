package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.handler.PacketHandler;
import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class JARCraftinator {

    private static Map<InetAddress, PacketHandler> packetHandlerList;
    private static boolean running = true;

    public static void main(String[] args){
        packetHandlerList = new HashMap<>();

        // Start the server thread
        Thread serverThread = new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(25565);
            }catch(IOException ex){
                System.out.println("The port that the server tried to bind to (25565) was already taken by another service.");
                System.out.println("Make sure that you don't have any other instances of the server open and that you've turned of all other services on this port.");
                System.exit(1);
            }

            while(true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if((!packetHandlerList.keySet().contains(clientSocket.getInetAddress())) || packetHandlerList.get(clientSocket.getInetAddress()).isDisconnected()) {
                        log("Registered new handler for " + clientSocket.getInetAddress());
                        PacketHandler connection = new PacketHandler(clientSocket);
                        packetHandlerList.put(clientSocket.getInetAddress(), connection);
                        connection.start();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        serverThread.start();

        // Now start the CLI
        Scanner scanner = new Scanner(System.in);
        while(running){
            String command = scanner.nextLine();

            if(command.equalsIgnoreCase("stop")){
                // Stop the server
                System.out.println("Stopping server...");
                for(PacketHandler connection : packetHandlerList.values()){
                    endConnection(connection);
                }
                serverThread.interrupt();
                running = false;
                System.out.println("Thanks for using JARCraftinator :)");
                System.exit(0);
            }
        }

    }

    public static String getIPAddress(Socket clientSocket){
        return clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
    }

    public static void log(String... messages){
        String output = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ";
        for(String message : messages){
            output += message + " ";
        }
        System.out.println(output);
    }

    public static void endConnection(PacketHandler connection){
        connection.interrupt();
        packetHandlerList.remove(connection);
    }

}