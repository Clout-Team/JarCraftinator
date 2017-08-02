package com.cloutteam.jarcraftinator.logging;

import org.fusesource.jansi.AnsiConsole;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fusesource.jansi.Ansi.*;

public class Logger {

    public Logger(){
        AnsiConsole.systemInstall();
    }

    public void log(String message){
        log(message, LogLevel.INFO);
    }

    public void log(String message, LogLevel level){
        String timestamp = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]";
        String prefix = " [" + level.toString() + "] ";
        message = message + ansi().reset();

        switch(level){
            case DEBUG:
                System.out.println(timestamp + prefix + message);
                break;
            case INFO:
                System.out.println(timestamp + prefix + message);
                break;
            case WARNING:
                // orange
                System.out.println(timestamp + ansi().fgYellow() + prefix + message);
                break;
            case ERROR:
                // light red
                System.out.println(timestamp + ansi().fgBrightRed() + prefix + message);
                break;
            case CRITICAL:
                // dark red
                System.out.println(timestamp + ansi().bgRed() + prefix + message);
                break;
            default:
                return;
        }
    }

    public void destroy(){
        AnsiConsole.systemUninstall();
    }

}
