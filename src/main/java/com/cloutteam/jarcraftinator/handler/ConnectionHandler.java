package com.cloutteam.jarcraftinator.handler;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.entity.player.PlayerConnection;
import com.cloutteam.jarcraftinator.logging.LogLevel;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler extends Thread {

	private final int port;
	private List<PlayerConnection> connectionList;
	@Getter
	@Setter
	private ServerSocket serverSocket;

	public ConnectionHandler(int port) {
		this.port = port;
		connectionList = new ArrayList<>();
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			while (JARCraftinator.getINSTANCE().isRunning()) {
				Socket socket = getServerSocket().accept();
				PlayerConnection connection = new PlayerConnection(socket);
				connection.start();
				connectionList.add(connection);
			}
		} catch (IOException ex) {
			JARCraftinator.getLogger().log("An internal server error occurred.", LogLevel.CRITICAL);
			System.exit(1);
		}
	}

	public List<PlayerConnection> getAllPlayerConnections() {
		return connectionList;
	}

	public void closeConnection(PlayerConnection connection) {
		connection.interrupt();
		connectionList.remove(connection);
	}

}
