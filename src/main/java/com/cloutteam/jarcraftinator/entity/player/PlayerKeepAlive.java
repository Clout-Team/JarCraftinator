package com.cloutteam.jarcraftinator.entity.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.protocol.packet.PacketPlayOutKeepAlive;

import java.io.IOException;
import java.util.TimerTask;

class PlayerKeepAlive extends TimerTask {

	private PlayerConnection connection;

	public PlayerKeepAlive(PlayerConnection connection) {
		this.connection = connection;

		// 50ms = 1 tick
		JARCraftinator.getINSTANCE().getTimer().schedule(this, 0, 50);
	}

	public void run() {
		try {
			// Obviously we don't want to bother
			// sending KeepAlive packets to a disconnected client.
			if (connection.isInterrupted() || !connection.isLoggedIn()) {
				cancel();
				return;
			}

			new PacketPlayOutKeepAlive().send(connection.getOut());
		} catch (IOException ex) {
			// Failed to send PacketPlayOutKeepAlive.
			JARCraftinator.getLogger().log("Failed to send keep-alive packet to " + connection.getPlayer().getName()
					+ " (" + ex.getMessage() + ").", LogLevel.ERROR);
			cancel();
			connection.close();
		}
	}

}
