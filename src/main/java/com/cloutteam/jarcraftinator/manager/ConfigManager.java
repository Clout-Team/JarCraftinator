package com.cloutteam.jarcraftinator.manager;

import com.cloutteam.jarcraftinator.config.FileConfiguration;

public class ConfigManager {

    private final FileConfiguration config;

    private boolean onlineMode;
    private int port;
    private int maxPlayers;
    private String motd;
    private String favicon;

    public ConfigManager(FileConfiguration config) {
        this.config = config;
        reloadConfig();
    }

    public void reloadConfig() {
        onlineMode = config.getBoolean("online-mode", true);
        port = config.getInt("port", 25565);
        maxPlayers = config.getInt("max-players", 20);
        motd = config.getString("pinger.motd", "Minecraft Server");
        favicon = config.getString("pinger.favicon", "");
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public int getPort() {
        return port;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getMotd() {
        return motd;
    }

    public String getFavicon() {
        return favicon;
    }
}
