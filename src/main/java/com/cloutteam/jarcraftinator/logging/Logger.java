package com.cloutteam.jarcraftinator.logging;

public interface Logger {

    void log(String message);
    void log(String message, LogLevel level);
    void log(String message, LogLevel level, boolean alwaysShow);

    void destroy();

}
