package com.cloutteam.jarcraftinator.logging;

import com.cloutteam.jarcraftinator.JARCraftinator;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fusesource.jansi.Ansi.*;

public class Logger {

    private File logFile;
    private FileWriter logWriter;

    public Logger(){
        AnsiConsole.systemInstall();

        if(!(logFile = new File("logs/latest.log")).exists()){
            new File("logs").mkdirs();
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                JARCraftinator.getLogger().log("Unable to create log file!", LogLevel.CRITICAL);
                System.exit(1);
            }
        }else{
            int i = 0;
            while(new File("logs/log-" + new SimpleDateFormat("dd-MM-YYYY").format(new Date()) + "-" + i + ".log").exists()){
                i++;
            }

            File archive = new File("logs/log-" + new SimpleDateFormat("dd-MM-YYYY").format(new Date()) + "-" + i + ".log");
            logFile.renameTo(archive);

            logFile = new File("logs/latest.log");
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                JARCraftinator.getLogger().log("Unable to create log file!", LogLevel.CRITICAL);
                System.exit(1);
            }
        }

        try {
            logWriter = new FileWriter(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String message){
        log(message, LogLevel.INFO);
    }

    public void log(String message, LogLevel level){
        String timestamp = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]";
        String prefix = " [" + level.toString() + "] ";

        try {
            String logmsg = message;
            if(JARCraftinator.getConfig() != null && JARCraftinator.getConfig().getBoolean("logging.strip-ansi-escape")){
                logmsg = logmsg.replaceAll("\u001B\\[[\\d;]*[^\\d;]","");
                logmsg = logmsg.replaceAll("\\e\\[[\\d;]*[^\\d;]","");
            }

            logWriter.write(timestamp + prefix + logmsg + System.getProperty("line.separator"));
            logWriter.flush();
        }catch(IOException ex){
            try {
                logWriter.close();
            }catch(Exception e){}

            System.out.println("Critical error whilst writing log to file. Exiting...");
            System.exit(1);
        }

        switch(level){
            case DEBUG:
                if(JARCraftinator.getConfig().getBoolean("logging.debug")) {
                    System.out.println(timestamp + prefix + message + ansi().reset());
                }
                break;
            case INFO:
                System.out.println(timestamp + prefix + message + ansi().reset());
                break;
            case WARNING:
                // orange
                System.out.println(timestamp + ansi().fgYellow() + prefix + message + ansi().reset());
                break;
            case ERROR:
                // light red
                System.out.println(timestamp + ansi().fgBrightRed() + prefix + message + ansi().reset());
                break;
            case CRITICAL:
                // dark red
                System.out.println(timestamp + ansi().bgRed() + prefix + message + ansi().reset());
                break;
            default:
                return;
        }
    }

    public void destroy(){
        AnsiConsole.systemUninstall();
        try {
            logWriter.close();
        }catch(IOException ex){
            System.out.println("Unable to close log file! Perhaps it was deleted?");
        }
    }

}
