package com.cloutteam.jarcraftinator.logging;

import com.cloutteam.jarcraftinator.plugin.api.VerdigrisPluginBase;

public class PluginLogger implements Logger {

    private VerdigrisPluginBase plugin;
    private JARCraftinatorLogger systemLogger;

    /**
     * Instantiates a logger for a specific plugin.
     * This automatically adds that plugin's name to the log.
     *
     * @param systemLogger The main logger for the server.
     * @param plugin The plugin that this logger is for.
     */
    public PluginLogger(JARCraftinatorLogger systemLogger, VerdigrisPluginBase plugin){
        this.systemLogger = systemLogger;
        this.plugin = plugin;
    }

    @Override
    public void log(String message) {
        log(message, LogLevel.INFO, false);
    }

    @Override
    public void log(String message, LogLevel level) {
        log(message, level, false);
    }

    @Override
    public void log(String message, LogLevel level, boolean alwaysShow) {
        systemLogger.log("[" + plugin.getName() + "] " + message, level, alwaysShow);
    }

    @Override
    public void destroy() {}

}
