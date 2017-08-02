package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.config.FileConfiguration;
import com.cloutteam.jarcraftinator.handler.ConnectionHandler;
import com.cloutteam.jarcraftinator.handler.PacketHandler;

import java.io.IOException;
import java.net.ServerSocket;
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

        // Check the port before the server starts
        try {
            ServerSocket portCheck = new ServerSocket(getConfig().getInt("port"));
            portCheck.close();
        }catch(IOException ex){
            JARCraftinator.err("Port " + getConfig().getInt("port") + " is already in use!\n\nCheck that:\n1. There isn't another application running on port " + getConfig().getInt("port") + ".\n2. There aren't other instances of the server still running.");
            System.exit(1);
        }

        System.out.println("Starting server...");
        packetHandlerList = new HashMap<>();
        new ConnectionHandler(getConfig().getInt("port")).start();
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
        if(getConfig().getBoolean("debug")) {
            String output = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ";
            for (String message : messages) {
                output += message + " ";
            }
            System.out.println(output);
        }
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