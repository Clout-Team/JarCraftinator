package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.config.FileConfiguration;
import com.cloutteam.jarcraftinator.handler.ConnectionHandler;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class JARCraftinator {

    private static boolean running = true;
    private static FileConfiguration config;
    private static Logger logger;

    private static int nextEntityID = 0;
    private static int nextTeleportID = 0;

    public static void main(String[] args) {
        System.out.println("Welcome to JARCraftinator.");
        System.out.println("JARCraftinator is a Clout Team project");
        System.out.println("https://wwww.clout-team.com/");
        System.out.println();
        System.out.println("Loading logger...");
        logger = new Logger();
        logger.log("Logger ready!");
        System.out.println();
        logger.log("Loading settings...");
        try {
            config = new FileConfiguration("server.yml");
            config.saveDefaultConfig(JARCraftinator.class.getResourceAsStream("/server.yml"));
            config.loadConfig();
            logger.log("Loaded settings.");
        } catch (IOException ex) {
            logger.log("Unable load server properties. Please double-check your syntax (remember: no tabs in a YAML file, only spaces).", LogLevel.CRITICAL);
            System.exit(1);
        }

        // Check the port before the server starts
        try {
            ServerSocket portCheck = new ServerSocket(getConfig().getInt("port"));
            portCheck.close();
        }catch(IOException ex){
            logger.log("Port " + getConfig().getInt("port") + " is already in use!\n\nCheck that:\n1. There isn't another application running on port " + getConfig().getInt("port") + ".\n2. There aren't other instances of the server still running.", LogLevel.CRITICAL);
            System.exit(1);
        }

        logger.log("Starting server...");
        new ConnectionHandler(getConfig().getInt("port")).start();

        // Now start the CLI
        Scanner scanner = new Scanner(System.in);
        logger.log("Server ready and online!");
        while (running) {
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("stop")) {
                // Stop the server
                logger.log("Stopping server...");
                running = false;

                // Close the ANSI console
                logger.destroy();

                // Bye bye :)
                System.out.println("Thanks for using JARCraftinator :)");
                System.exit(0);
            } else {
                logger.log("Unknown command.");
            }
        }

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

    public static Logger getLogger(){
        return logger;
    }

}