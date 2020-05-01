package com.cloutteam.jarcraftinator.entity.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.api.chat.ChatColor;
import com.cloutteam.jarcraftinator.api.json.JSONObject;
import com.cloutteam.jarcraftinator.exceptions.IOWriteException;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.protocol.ConnectionState;
import com.cloutteam.jarcraftinator.protocol.packet.*;
import com.cloutteam.jarcraftinator.utils.UUIDManager;
import com.cloutteam.jarcraftinator.utils.VarData;
import com.cloutteam.jarcraftinator.world.Chunk;
import com.cloutteam.jarcraftinator.world.Difficulty;
import com.cloutteam.jarcraftinator.world.DimensionType;
import com.cloutteam.jarcraftinator.world.LevelType;
import com.cloutteam.jarcraftinator.world.navigation.Location;
import com.cloutteam.jarcraftinator.world.navigation.Teleport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

public class PlayerConnection extends Thread {

	private final Socket socket;

	private ConnectionState connectionState = ConnectionState.HANDSHAKE;
	private boolean loggedIn = false;
	private Player player;

	private DataInputStream in;
	private DataOutputStream out;

	public PlayerConnection(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			JARCraftinator.getLogger().log("Unable to initiate two-way contact with " + socket.getInetAddress() + ":"
					+ socket.getPort() + ". (" + ex.getMessage() + ")!", LogLevel.ERROR);

			try {
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException exception) {
				JARCraftinator.getLogger().log("Additionally, an error occured whilst trying to close the "
						+ "connection. (" + exception.getMessage() + ")", LogLevel.ERROR);
			}

			interrupt();
			return;
		}

		while (!socket.isClosed()) {
			try {
				int packetLength = VarData.readVarInt(in);
				int packetId = VarData.readVarInt(in);
				
				System.out.println("Got Packet: "+packetId);

				System.out.println("Connection: "+connectionState);
				switch (connectionState) {
				case HANDSHAKE:
					switch (packetId) {
					case MinecraftPacket.HANDSHAKE.modern:
						try {
							// Receive the handshake and set the next status
							PacketHandshakeIn handshake = new PacketHandshakeIn();
							handshake.onReceive(packetLength, in);
							connectionState = handshake.getNextState() == PacketHandshakeIn.NextState.LOGIN
									? ConnectionState.LOGIN
									: ConnectionState.STATUS;
						} catch (IOException | IOWriteException ex) {
							JARCraftinator.getLogger().log(
									"Failed to handshake with " + socket.getInetAddress() + ":" + socket.getPort(),
									LogLevel.ERROR);
							JARCraftinator.getLogger().log(ex.getMessage(), LogLevel.DEBUG);
						}
						break;
					case MinecraftPacket.HANDSHAKE.legacy:
						// TODO legacy ping
						break;
					}
					break;
				case STATUS:
					switch (packetId) {
					case 0x00:
						// Empty packet
						// Send the response back to the client
						new PacketStatusOutResponse(JARCraftinator.protocolVersion,
								JARCraftinator.getINSTANCE().getConfigManager().getMaxPlayers(), 0, null,
								ChatColor.translateAlternateColorCodes(
										JARCraftinator.getINSTANCE().getConfigManager().getMotd()),
								JARCraftinator.getINSTANCE().getConfigManager().getFavicon()).send(out);

						break;
					case 0x01:
						try {
							JARCraftinator.getLogger()
									.log(socket.getInetAddress() + ":" + socket.getPort() + " has pinged the server.");
							PacketStatusInPing ping = new PacketStatusInPing();
							ping.onReceive(packetLength, in);
							new PacketStatusOutPong(ping.getLength(), ping.getData()).send(out);
							socket.close();
						} catch (IOException ex) {
							JARCraftinator.getLogger()
									.log("Error whilst handling server ping (" + ex.getMessage() + ")", LogLevel.DEBUG);
						}
						break;
					}
					break;
				case LOGIN:
					switch (packetId) {
					case MinecraftPacket.LOGIN.LOGIN_START.in:
						String username;

						try {
							PacketLoginInLoginStart login = new PacketLoginInLoginStart();
							login.onReceive(packetLength, in);
							username = login.getPlayerName();
						} catch (IOException ex) {
							JARCraftinator.getLogger().log(
									"Error whilst handling player login (" + ex.getMessage() + ")", LogLevel.DEBUG);
							break;
						}

						UUID uuid = UUIDManager.getUUID(username);

						player = new Player(username, uuid, this);
						player.loadFromStorage();
						JARCraftinator.getINSTANCE().getPlayerManager().addOnline(player);
						new PacketLoginOutLoginSuccess(uuid, username).send(out);
						connectionState = ConnectionState.PLAY;
						PacketPlayOutSpawnPosition spawnPacket = new PacketPlayOutSpawnPosition(
								(int) Math.round(player.getLocation().getX()),
								(int) Math.round(player.getLocation().getY()),
								(int) Math.round(player.getLocation().getZ()));
						spawnPacket.send(out);
						new PacketPlayOutJoinGame(player.getEntityId(), GameMode.CREATIVE, DimensionType.OVERWORLD,
								Difficulty.PEACEFUL, 10, LevelType.DEFAULT, false).send(out);
						JARCraftinator.getLogger()
								.log("Player " + username + " has logged in from " + socket.getInetAddress() + ":"
										+ socket.getPort() + " with UUID " + uuid.toString() + ".");
						JARCraftinator.getLogger().log(username + " has spawned on the server at (" + spawnPacket.getX()
								+ ", " + spawnPacket.getY() + ", " + spawnPacket.getZ() + ").");
						System.out.println("Connection State: "+connectionState);
						break;
					case 0x01:
						// TODO encryption response
						break;
					}
					break;
				case PLAY:
					switch (packetId) {
					case MinecraftPacket.PLAY.TELEPORT_CONFIRM.in:
						try {
							PacketPlayInTeleportConfirm teleportConfirm = new PacketPlayInTeleportConfirm();
							teleportConfirm.onReceive(packetLength, in);
							if (JARCraftinator.getINSTANCE().getTeleportManager()
									.confirmTeleport(teleportConfirm.getTeleportID()))
								JARCraftinator.getLogger().log("Confirmed teleport: " + teleportConfirm.getTeleportID(),
										LogLevel.DEBUG);
							else
								JARCraftinator.getLogger()
										.log("Failed to confirm teleport " + teleportConfirm.getTeleportID()
												+ " because it's either already confirmed or it doesn't exist.",
												LogLevel.DEBUG);
						} catch (IOException ex) {
							JARCraftinator.getLogger().log(
									"Error whilst confirming teleportation (" + ex.getMessage() + ")", LogLevel.DEBUG);
						}
						break;
					case MinecraftPacket.PLAY.CHAT.in:
						try {
							PacketPlayInChat chatPacket = new PacketPlayInChat();
							chatPacket.onReceive(packetLength, in);

							if (!chatPacket.isValid()) {
								// TODO: Kick
								JARCraftinator.getLogger().log(player.getName()
										+ " sent a message of length greater than 256 characters. This is not normally possible.",
										LogLevel.WARNING);
								socket.close();
							}

							// Handle command
							if (chatPacket.getMessage().startsWith("/")) {
								String response = ChatColor
										.translateAlternateColorCodes("&c&o&lThis command was not found.");
								JSONObject chatComponent = new JSONObject();
								chatComponent.add("text", response);
								PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatComponent.toString(),
										PacketPlayOutChat.PacketPlayOutChatPosition.SYSTEM_MESSAGE);
								packetPlayOutChat.send(out);
								break;
							}

							JARCraftinator.getLogger().log("[" + getPlayer().getName() + "] " + chatPacket.getMessage(),
									LogLevel.CHAT);

							JSONObject chatComponent = new JSONObject();
							chatComponent.add("text", getPlayer().getName() + " > " + chatPacket.getMessage());
							PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatComponent.toString(),
									PacketPlayOutChat.PacketPlayOutChatPosition.CHAT_BOX);

							for (PlayerConnection connection : JARCraftinator.getINSTANCE().getConnectionHandler()
									.getAllPlayerConnections()) {
								packetPlayOutChat.send(connection.getOut());
							}
						} catch (IOException ex) {
							JARCraftinator.getLogger().log("Error whilst receiving message (" + ex.getMessage() + ")",
									LogLevel.DEBUG);
						}
						break;
					case MinecraftPacket.PLAY.CLIENT_SETTINGS.in:
						try {
							PacketPlayInClientSettings clientSettings = new PacketPlayInClientSettings();
							clientSettings.onReceive(packetLength, in);
							if (loggedIn)
								break;
							loggedIn = true;
							System.out.println("Player's locale: " + clientSettings.getLocale());
							new PacketPlayOutPlayerPositionAndLook(player.getLocation().getX(),
									player.getLocation().getY(), player.getLocation().getZ(),
									player.getLocation().getYaw(), player.getLocation().getPitch(), (byte) 0, // flags
									new Teleport(player, null, player.getLocation(), Teleport.TeleportCause.LOGIN)
											.getId()).send(out);

							//int chunkX = (int) Math.floor(player.getLocation().getX() / 16);
							//int chunkZ = (int) Math.floor(player.getLocation().getZ() / 16);
							//for (int x = chunkX - clientSettings.getViewDistance(); x < chunkX
							//		+ clientSettings.getViewDistance(); x++)
							//	for (int z = chunkZ - clientSettings.getViewDistance(); z < chunkZ
							//			+ clientSettings.getViewDistance(); z++)
							//		new PacketPlayOutChunkData(new Chunk(player.getLocation().getWorld(), x, z))
							//				.send(out);
							//
							// Start the KeepAlive runnable!
							//new PlayerKeepAlive(this);
						} catch (IOException ex) {
							JARCraftinator.getLogger().log(
									"Error whilst handling client settings packet (" + ex.getMessage() + ")",
									LogLevel.DEBUG);
						}
						break;
					case MinecraftPacket.PLAY.KEEP_ALIVE.in:
						PacketPlayInKeepAlive packetPlayInKeepAlive = new PacketPlayInKeepAlive();
						packetPlayInKeepAlive.onReceive(packetLength, in);
						break;
					case MinecraftPacket.PLAY.PLAYER_POSITION_AND_LOOK.in:
						try {
							PacketPlayInPlayerPositionAndLook packetPlayInPlayerPositionAndLook = new PacketPlayInPlayerPositionAndLook();
							packetPlayInPlayerPositionAndLook.onReceive(packetLength, in);

							player.updateLocation(new Location(player.getWorld(),
									packetPlayInPlayerPositionAndLook.getX(), packetPlayInPlayerPositionAndLook.getY(),
									packetPlayInPlayerPositionAndLook.getZ(),
									packetPlayInPlayerPositionAndLook.getYaw(),
									packetPlayInPlayerPositionAndLook.getPitch()));
						} catch (IOException ex) {
							JARCraftinator.getLogger().log(
									"Error whilst handling player move packet (" + ex.getMessage() + ")",
									LogLevel.DEBUG);
						}
						break;
					default:
						System.out.println("Unknown packet ID: 0x" +Integer.toHexString(packetId));

						try {
							for (packetLength -= VarData.getVarInt(packetId).length; packetLength > 0; packetLength--)
								in.readByte();
						} catch (IOException ex) {
							ex.printStackTrace();
						}

					}
					break;
				}
			} catch (EOFException | SocketException e) {
				JARCraftinator.getLogger().log(
						"Error while receiving packet from " + socket.getInetAddress().toString() + "! Ignoring...",
						LogLevel.DEBUG);
				break;
			} catch (IOException ex) {
				if (socket.isClosed()) {
					break;
				}
			}
		}

		if (loggedIn) {
			JARCraftinator.getLogger().log(getPlayer().getName() + " has quit the server!");
			loggedIn = false;
		}

		close();
	}

	public ConnectionState getConnectionState() {
		return connectionState;
	}

	public DataInputStream getIn() {
		return in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public Player getPlayer() {
		return player;
	}

	boolean isLoggedIn() {
		return loggedIn;
	}

	public void close() {
		JARCraftinator.getINSTANCE().getConnectionHandler().closeConnection(this);
	}

}
