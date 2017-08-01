package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.config.FileConfiguration;
import com.cloutteam.jarcraftinator.handler.ConnectionHandler;
import com.cloutteam.jarcraftinator.handler.PacketHandler;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JARCraftinator {

    private static Map<String, PacketHandler> packetHandlerList;
    private static boolean running = true;
    private static FileConfiguration config;

    private static int nextEntityID = 0;
    private static int nextTeleportID = 0;

    public static void main(String[] args) {
        System.out.println("Welcome to JARCraftinator.");
        System.out.println("JARCraftinator is a Clout Team project");
        System.out.println("https://wwww.clout-team.com/");
        System.out.println();
        System.out.println("Loading settings...");
        try {
            config = new FileConfiguration("server.yml");
            config.saveDefaultConfig(JARCraftinator.class.getResourceAsStream("/server.yml"));
            config.loadConfig();
            System.out.println("Loaded settings.");
        } catch (IOException ex) {
            System.out.println("Unable load server properties. Please double-check your syntax (remember: no tabs in a YAML file, only spaces).");
        }
        System.out.println("Starting server...");
        packetHandlerList = new HashMap<>();

        /*// Start the server thread
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
                    if((!packetHandlerList.keySet().contains(clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort())) || packetHandlerList.get(clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort()).isDisconnected()) {
                        log("Registered new handler for " + clientSocket.getInetAddress());
                        PacketHandler connection = new PacketHandler(clientSocket);
                        packetHandlerList.put(clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort(), connection);
                        connection.start();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        serverThread.start();*/
        new ConnectionHandler().start();
        System.out.println("Server ready and listening.");

        System.out.println("Starting CLI...");
        // Now start the CLI
        Scanner scanner = new Scanner(System.in);
        System.out.println("CLI ready!");
        while (running) {
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("stop")) {
                // Stop the server
                System.out.println("Stopping server...");
                for (PacketHandler connection : packetHandlerList.values()) {
                    endConnection(connection);
                }
                //serverThread.interrupt();
                running = false;
                System.out.println("Thanks for using JARCraftinator :)");
                System.exit(0);
            } else if (command.equalsIgnoreCase("handlers")) {
                System.out.println(packetHandlerList.toString());
            } else {
                System.out.println("Unknown command.");
            }
        }

    }

    public static String getIPAddress(Socket clientSocket) {
        return clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
    }

    public static void log(String... messages) {
        String output = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ";
        for (String message : messages) {
            output += message + " ";
        }
        System.out.println(output);
    }

    public static void err(String... messages) {
        String output = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ";
        for (String message : messages) {
            output += message + " ";
        }
        System.err.println(output);
    }

    public static void endConnection(PacketHandler connection) {
        connection.interrupt();
        packetHandlerList.remove(connection);
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static int getNextEntityID() {
        return nextEntityID++;
    }

    public static int getNextTeleportID() {
        return nextTeleportID++;
    }

}