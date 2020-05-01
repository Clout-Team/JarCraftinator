package com.cloutteam.jarcraftinator.logging;

import com.cloutteam.jarcraftinator.JARCraftinator;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fusesource.jansi.Ansi.*;

public class JARCraftinatorLogger implements Logger {

	private File logFile;
	private FileWriter logWriter;

	public JARCraftinatorLogger() {
		AnsiConsole.systemInstall();

		if (!(logFile = new File("logs/latest.log")).exists()) {
			new File("logs").mkdirs();
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				JARCraftinator.getLogger().log("Unable to create log file!", LogLevel.CRITICAL);
				System.exit(1);
			}
		} else {
			int i = 0;
			while (new File("logs/log-" + new SimpleDateFormat("YYYY-MM-dd").format(new Date()) + "-" + i + ".log")
					.exists()) {
				i++;
			}

			File archive = new File(
					"logs/log-" + new SimpleDateFormat("YYYY-MM-dd").format(new Date()) + "-" + i + ".log");
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
		if (level == LogLevel.CHAT && !(JARCraftinator.getINSTANCE().getConfigManager().shouldLogChatMessages()))
			return;

		String timestamp = "[" + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()) + "]";
		String prefix = " [" + level.toString() + "] ";

		try {
			String logmsg = message;
			if (JARCraftinator.getINSTANCE().getConfigManager() != null
					&& JARCraftinator.getINSTANCE().getConfigManager().isStripAnsiEscape()) {
				logmsg = logmsg.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
				logmsg = logmsg.replaceAll("\\e\\[[\\d;]*[^\\d;]", "");
			}

			logWriter.write(timestamp + prefix + logmsg + System.getProperty("line.separator"));
			logWriter.flush();
		} catch (IOException ex) {
			try {
				logWriter.close();
			} catch (Exception e) {
			}

			System.out.println("Critical error whilst writing log to file. Exiting...");
			System.exit(1);
		}

		switch (level) {
		case DEBUG:
			if (JARCraftinator.getINSTANCE().getConfigManager().isDebug() || alwaysShow) {
				System.out.println(timestamp + prefix + message + ansi().reset());
			}
			break;
		case CHAT:
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
		}
	}

	@Override
	public void destroy() {
		AnsiConsole.systemUninstall();
		try {
			logWriter.close();
		} catch (IOException ex) {
			System.out.println("Unable to close log file! Perhaps it was deleted?");
		}
	}

}
