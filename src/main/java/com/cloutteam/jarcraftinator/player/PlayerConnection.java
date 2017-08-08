package com.cloutteam.jarcraftinator.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.api.*;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.protocol.ConnectionState;
import com.cloutteam.jarcraftinator.protocol.MinecraftVersion;
import com.cloutteam.jarcraftinator.protocol.packet.*;
import com.cloutteam.jarcraftinator.utils.UUIDManager;
import com.cloutteam.jarcraftinator.utils.VarData;

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
    private String username = "";

    public PlayerConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (!socket.isClosed()) {
                try {
                    int packetLength = VarData.readVarInt(in);
                    int packetId = VarData.readVarInt(in);

                    switch (connectionState) {
                        case HANDSHAKE:
                            switch (packetId) {
                                case 0x00:
                                    // Receive the handshake and set the next status
                                    PacketHandshakeIn handshake = new PacketHandshakeIn();
                                    handshake.onReceive(packetLength, in);
                                    connectionState = handshake.getNextState() == PacketHandshakeIn.NextState.LOGIN ? ConnectionState.LOGIN : ConnectionState.STATUS;
                                    break;
                                case 0xFE:
                                    // TODO legacy ping
                                    break;
                            }
                            break;
                        case STATUS:
                            switch (packetId) {
                                case 0x00:
                                    // Empty packet
                                    // Send the response back to the client
                                    new PacketStatusOutResponse(MinecraftVersion.v1_12_1, JARCraftinator.getConfig().getInt("max-players"), 0, null, ChatColor.translateAlternateColorCodes(JARCraftinator.getConfig().getString("pinger.motd")), JARCraftinator.getConfig().getString("pinger.favicon")).send(out);
                                    JARCraftinator.getLogger().log(socket.getInetAddress() + ":" + socket.getPort() + " has pinged the server.");
                                    break;
                                case 0x01:
                                    PacketStatusInPing ping = new PacketStatusInPing();
                                    ping.onReceive(packetLength, in);
                                    new PacketStatusOutPong(ping.getLength(), ping.getData()).send(out);
                                    socket.close();
                                    break;
                            }
                            break;
                        case LOGIN:
                            switch (packetId) {
                                case 0x00:
                                    PacketLoginInLoginStart login = new PacketLoginInLoginStart();
                                    login.onReceive(packetLength, in);
                                    username = login.getPlayerName();

                                    UUID uuid = UUIDManager.getUUID(username);
                                    new PacketLoginOutLoginSuccess(uuid, username).send(out);
                                    connectionState = ConnectionState.PLAY;
                                    new PacketPlayOutJoinGame(JARCraftinator.getNextEntityID(), GameMode.SURVIVAL, DimensionType.OVERWORLD, Difficulty.PEACEFUL, 10, LevelType.DEFAULT, false).send(out);
                                    PacketPlayOutSpawnPosition spawnPacket = new PacketPlayOutSpawnPosition(0, 64, 0);
                                    spawnPacket.send(out);
                                    JARCraftinator.getLogger().log("Player " + username + " has logged in from " + socket.getInetAddress() + " with UUID " + uuid.toString() + ".");
                                    JARCraftinator.getLogger().log(username + " has spawned at " + spawnPacket.getX() + " " + spawnPacket.getY() + " " + spawnPacket.getZ() + ".");
                                    break;
                                case 0x01:
                                    // TODO encryption response
                                    break;
                            }
                            break;
                        case PLAY:
                            switch (packetId) {
                                case 0x00:
                                    PacketPlayInTeleportConfirm teleportConfirm = new PacketPlayInTeleportConfirm();
                                    teleportConfirm.onReceive(packetLength, in);
                                    JARCraftinator.getLogger().log("Confirmed teleportID: " + teleportConfirm.getTeleportID(), LogLevel.DEBUG);
                                    break;
                                case 0x04:
                                    PacketPlayInClientSettings clientSettings = new PacketPlayInClientSettings();
                                    clientSettings.onReceive(packetLength, in);
                                    if (loggedIn)
                                        break;
                                    loggedIn = true;
                                    JARCraftinator.getLogger().log("Player's locale: " + clientSettings.getLocale(), LogLevel.DEBUG);
                                    new PacketPlayOutPlayerPositionAndLook(0, 64, 0, 0, 0, (byte) 0, JARCraftinator.getNextTeleportID()).send(out);
                                    break;
                                case 0x0E:
                                    PacketPlayInPlayerPositionAndLook packetPlayInPlayerPositionAndLook = new PacketPlayInPlayerPositionAndLook();
                                    packetPlayInPlayerPositionAndLook.onReceive(packetLength, in);
                                    JARCraftinator.getLogger().log("X: " + packetPlayInPlayerPositionAndLook.getX(), LogLevel.DEBUG);
                                    JARCraftinator.getLogger().log("Y: " + packetPlayInPlayerPositionAndLook.getY(), LogLevel.DEBUG);
                                    JARCraftinator.getLogger().log("Z: " + packetPlayInPlayerPositionAndLook.getZ(), LogLevel.DEBUG);
                                    break;
                                default:
                                    JARCraftinator.getLogger().log("Unknown packet ID: " + Integer.toHexString(packetId), LogLevel.DEBUG);
                                    for (packetLength -= VarData.getVarInt(packetId).length; packetLength > 0; packetLength--)
                                        in.readByte();

                            }
                            break;
                    }
                } catch (EOFException | SocketException e) {
                    JARCraftinator.getLogger().log("Error while receiving packet from " + socket.getInetAddress().toString() + "! Closing connection...", LogLevel.ERROR);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
