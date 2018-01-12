package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.config.FileConfiguration;
import com.cloutteam.jarcraftinator.handler.ConnectionHandler;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.logging.Logger;
import com.cloutteam.jarcraftinator.manager.ConfigManager;
import com.cloutteam.jarcraftinator.manager.PlayerManager;
import com.cloutteam.jarcraftinator.manager.TeleportManager;
import com.cloutteam.jarcraftinator.protocol.MinecraftVersion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;

public class JARCraftinator {

    public static final MinecraftVersion protocolVersion = MinecraftVersion.v1_12_2;
    private static JARCraftinator instance;

    private boolean running = true;
    private FileConfiguration config;
    private ConfigManager configManager;
    private Logger logger;
    private String version;
    private TeleportManager teleportManager;
    private PlayerManager playerManager;
    private Timer timer;

    private JARCraftinator() {
        instance = this;
        // TODO: Track latest version from http://api.clout-team.com/verdigris/version/

        try {
            String path = "/META-INF/maven/com.clout-team/JARCraftinator/pom.properties";
            InputStream stream = JARCraftinator.class.getClass().getResourceAsStream(path);
            Properties properties = new Properties();
            properties.load(stream);
            version = (String) properties.get("version");
        } catch (IOException | NullPointerException ex) {
            version = "Unknown";
        }

        System.out.println("Welcome to JARCraftinator v" + getVersion() + " (Minecraft " +
                protocolVersion.getName() + ").");
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
            configManager = new ConfigManager(config);
            logger.log("Loaded settings.");
        } catch (IOException ex) {
            logger.log("Unable load server properties. Please double-check your syntax (remember: no tabs in a YAML file, only spaces).", LogLevel.CRITICAL);
            System.exit(1);
        }

        // Check the port before the server starts
        try {
            ServerSocket portCheck = new ServerSocket(configManager.getPort());
            portCheck.close();
        } catch (IOException ex) {
            logger.log("Port " + configManager.getPort() + " is already in use!\n\nCheck that:\n1. There isn't another application running on port " + configManager.getPort() + ".\n2. There aren't other instances of the server still running.", LogLevel.CRITICAL);
            System.exit(1);
        }

        logger.log("Starting server...");
        new ConnectionHandler(configManager.getPort()).start();

        logger.log("Starting scheduler...");
        timer = new Timer();

        logger.log("Loading teleport manager...");
        teleportManager = new TeleportManager();

        logger.log("Loading player manager...");
        playerManager = new PlayerManager();

        // Now start the CLI
        Scanner scanner = new Scanner(System.in);
        logger.log("Server ready and online!");
        while (running) {
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("stop")) {
                // End the timer
                logger.log("Closing scheduler...");
                timer.cancel();

                // Stop the server
                logger.log("Stopping server...");
                running = false;

                // Close the ANSI console
                logger.destroy();

                // Bye bye :)
                System.out.println("Thanks for using JARCraftinator :)");
                System.exit(0);
            } else if (command.equalsIgnoreCase("reload")) {
                logger.log("Reloading...");
                try {
                    config.loadConfig();
                } catch (FileNotFoundException ex) {
                    logger.log("Unable to find configuration file!", LogLevel.CRITICAL);
                    System.exit(1);
                }
                logger.log("Reload complete!");
            } else {
                logger.log("Unknown command.");
            }
        }
    }

    public static void main(String[] args) {
        new JARCraftinator();
    }

    public static ConfigManager getConfig() {
        return instance.configManager;
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    public static String getVersion() {
        return instance.version;
    }

    public static TeleportManager getTeleportManager() {
        return instance.teleportManager;
    }

    public static PlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static Timer getTimer(){
        return instance.timer;
    }

}